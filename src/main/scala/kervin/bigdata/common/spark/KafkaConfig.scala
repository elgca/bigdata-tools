package kervin.bigdata.common.spark

import kervin.bigdata.common.kafka.OffsetManager
import kervin.bigdata.common.kafka.OffsetRange.OffsetRangeTuple
import org.apache.kafka.common.TopicPartition
import org.apache.spark.streaming.kafka010.OffsetRange

case class KafkaConfig(
                        groupId: String,
                        batchDuration: Option[Long],
                        bootstrap: String,
                        topics: List[String],
                        params: Map[String, String],
                        sparkConf: Map[String, String],
                        offsetManager: Option[OffsetManager]) {
  def latestOffsets: List[OffsetRange] = {
    offsetManager.map {
      db =>
        db.selectOffset(groupId).map(x => toOffsetRange(x.toTuple))
    }.getOrElse(Nil)
  }

  def latestOffsetsMap: Map[TopicPartition, Long] = {
    latestOffsets.map(x => x.topicPartition() -> x.untilOffset).toMap
  }

  def toOffsetRange(t: (String, Int, Long, Long)) =
    OffsetRange(t._1, t._2, t._3, t._4)

  def toTuple(offsetRange: OffsetRange): OffsetRangeTuple = (offsetRange.topic, offsetRange.partition, offsetRange.fromOffset, offsetRange.untilOffset)
}
