package kervin.bigdata.common.kafka

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

  def selectOffset(c: String): List[OffsetRange.OffsetRangeTuple] = {
    database.query[ConsumerRecord].whereEqual("consumer", c).order("id", true).fetchOne().map {
      line =>
        database.query[OffsetRange].whereEqual("line", line)
          .fetch().map(_.toTuple).toList
    }.getOrElse(Nil)
  }

  def updateOffsets(c: String, offsets: Seq[OffsetRange.OffsetRangeTuple]): Unit = {
    if (offsets == null || offsets.isEmpty) return
    database.transaction {
      val record = database.save(ConsumerRecord(c))
      offsets.foreach {
        x =>
          database.save(OffsetRange(record, x))
      }
    }
  }
}
