package kervin.bigdata.common.kafka

import org.apache.spark.streaming.kafka010
import sorm.{Entity, InitMode, Instance}

case class OffsetManager(
                          url: String,
                          user: String,
                          password: String,
                          initMode: InitMode,
                          poolSize: Int
                        )
  extends Instance(
    entities = Set() + Entity[ConsumerRecord](
      indexed = Set() + Seq("consumer")) +
      Entity[OffsetRange](
        indexed = Set() + Seq("line")
      ),
    url = url,
    user = user,
    password = password,
    initMode = initMode,
    poolSize = poolSize
  ) {
  database =>

  def selectOffset(c: String): List[kafka010.OffsetRange] = {
    database.query[ConsumerRecord].whereEqual("consumer", c).order("id", true).fetchOne().map {
      line =>
        database.query[OffsetRange].whereEqual("line", line)
          .fetch().map(_.toOffsetRange).toList
    }.getOrElse(Nil)
  }

  def updateOffsets(c: String, offsets: Seq[kafka010.OffsetRange]): Unit = {
    if (offsets == null || offsets.isEmpty) return
    database.transaction {
      val record = database.save(ConsumerRecord(c))
      offsets.foreach {
        x =>
          database.save(OffsetRange.fromOffsetRange(record, x))
      }
    }
  }
}
