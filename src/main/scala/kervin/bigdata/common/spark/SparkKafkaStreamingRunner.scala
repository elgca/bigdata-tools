package kervin.bigdata.common.spark

import java.io.{PrintWriter, StringWriter}

import kervin.bigdata.common.config.AppConfig
import kervin.bigdata.common.email.EMailSender
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.internal.Logging
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Milliseconds, StreamingContext}
import org.joda.time.DateTime
/**
* 以Kafka作为数据源的spark streaming类，可选择由kafka本身管理offset或则
* 由mysql管理offset；并提供邮件通知功能
*/
trait SparkKafkaStreamingRunner extends Serializable with Logging {
  @transient var emailSender: EMailSender = _

  def setConf(conf: SparkConf): SparkConf = conf

  def run(rdd: RDD[ConsumerRecord[String, String]]): Unit

  def getTitle(tType: String, name: String, msg: String) =
    s"[SPARK][Streaming][$tType][$name][${new DateTime()}]$msg"

  def main(args: Array[String]): Unit = {
    AppConfig.loadArgs(args)
    val kafkaConfig = AppConfig.loadConfigOrThrow[KafkaConfig]("kafka")
    emailSender = EMailSender(AppConfig.getConfig)
    val conf = setConf {
      new SparkConf()
        .setAll(kafkaConfig.sparkConf)
    }
    //kafka params
    val kafkaParams: Map[String, Object] =
      kafkaConfig.params ++
        Map[String, Object](
          "key.deserializer" -> classOf[StringDeserializer],
          "value.deserializer" -> classOf[StringDeserializer],
          "group.id" -> kafkaConfig.groupId,
          "bootstrap.servers" -> kafkaConfig.bootstrap,
          "enable.auto.commit" -> (false: java.lang.Boolean)
        )
    val topics = kafkaConfig.topics.toSet
    val fromOffset = kafkaConfig.latestOffsetsMap
    val initMsg =
      s"""
         |# 配置初始化完成
         |
         |```conf
         |topics: ${topics.mkString("[", ",", "]")}
         |params: ${kafkaParams.map(x => "        " + x._1 + " : " + x._2).map("  " + _).mkString("{\n", "\n", "\n}")}
         |spark-conf: ${fromOffset.map(x => "        " + x._1 + " : " + x._2).map("  " + _).mkString("{\n", "\n", "\n}")}
         |from-offsets: ${fromOffset.map(x => "        " + x._1 + " : " + x._2).map("  " + _).mkString("{\n", "\n", "\n}")}
         |```
      """.stripMargin
    log.info(initMsg)
    emailSender.send("START", initMsg)
    //create DStream
    val ssc = new StreamingContext(conf, Milliseconds(kafkaConfig.batchDuration.get))
    ssc.sparkContext.setLogLevel("WARN")

    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams, fromOffset)
    )

    stream.foreachRDD {
      rdd =>
        if (!rdd.isEmpty()) {
          val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
          //处理数据
          try {
            run(rdd)
            kafkaConfig.offsetManager match {
              case Some(db) => db.updateOffsets(kafkaConfig.groupId, offsetRanges.map(kafkaConfig.toTuple))
              case None => stream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
            }
          } catch {
            case e: Throwable =>
              val str = new StringWriter()
              val pt = new PrintWriter(str)
              e.printStackTrace(pt)
              emailSender.send(
                getTitle("ERROR", ssc.sparkContext.appName, e.getMessage),
                s"""# 错误日志
                   |```java
                   |${str.toString}
                   |```""".stripMargin)
              throw e
          }
        }
    }

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
    System.exit(0)
  }
}
