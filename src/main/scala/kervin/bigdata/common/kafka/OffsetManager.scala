package kervin.bigdata.common.kafka

import sorm.{Entity, InitMode, Instance, Persisted}

case class OffsetManager(
                          url: String,
                          user: String,
                          password: String,
                          initMode: InitMode = InitMode.Create,
                          poolSize: Int = 1
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

  def selectOffset(c: String): List[OffsetRange] = {
    database.query[ConsumerRecord].whereEqual("consumer", c).order("id", reverse = true).fetchOne().map {
      line =>
        database.query[OffsetRange].whereEqual("line", line)
          .fetch().toList
    }.getOrElse(Nil)
  }

  def updateOffsets(c: String, offsets: Seq[OffsetRange.OffsetRangeTuple]): Unit = {
    if (offsets == null || offsets.isEmpty) return
    database.transaction {
      val topics = offsets.map(_._1).distinct
      val record = database.save(ConsumerRecord(c, topics))
      offsets.foreach {
        x =>
          database.save(OffsetRange(record, x))
      }
    }
  }
}
