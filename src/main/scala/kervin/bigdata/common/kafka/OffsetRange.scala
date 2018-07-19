package kervin.bigdata.common.kafka

import org.apache.kafka.common.TopicPartition
import org.joda.time.DateTime

case class OffsetRange(line: ConsumerRecord,
                       topic: String,
                       partition: Int,
                       fromOffset: Long,
                       untilOffset: Long) {

  import OffsetRange.OffsetRangeTuple

  def topicPartition(): TopicPartition = new TopicPartition(topic, partition)

  def count(): Long = untilOffset - fromOffset

  override def equals(obj: Any): Boolean = obj match {
    case that: OffsetRange =>
      this.topic == that.topic &&
        this.partition == that.partition &&
        this.fromOffset == that.fromOffset &&
        this.untilOffset == that.untilOffset
    case _ => false
  }

  override def hashCode(): Int = {
    toTuple.hashCode()
  }

  override def toString(): String = {
    s"OffsetRange(line:'${line}',topic: '$topic', partition: $partition, range: [$fromOffset -> $untilOffset])"
  }

  /** this is to avoid ClassNotFoundException during checkpoint restore */
  def toTuple: OffsetRangeTuple = (topic, partition, fromOffset, untilOffset)

  //  def toOffsetRange: KOffsetRange =
  //    KOffsetRange(topic, partition, fromOffset, untilOffset)
}

object OffsetRange {
  def create(record: ConsumerRecord,
             topicPartition: TopicPartition,
             fromOffset: Long,
             untilOffset: Long): OffsetRange =
    new OffsetRange(record, topicPartition.topic, topicPartition.partition, fromOffset, untilOffset)

  def apply(record: ConsumerRecord,
            topicPartition: TopicPartition,
            fromOffset: Long,
            untilOffset: Long): OffsetRange =
    new OffsetRange(record, topicPartition.topic, topicPartition.partition, fromOffset, untilOffset)

  /** this is to avoid ClassNotFoundException during checkpoint restore */
  type OffsetRangeTuple = (String, Int, Long, Long)

  //  def fromOffsetRange(record: ConsumerRecord, offsetRange: OffsetRangeTuple): OffsetRange = {
  //    OffsetRange(record, offsetRange.topic, offsetRange.partition, offsetRange.fromOffset, offsetRange.untilOffset)
  //  }

  def apply(record: ConsumerRecord, t: OffsetRangeTuple) =
    new OffsetRange(record, t._1, t._2, t._3, t._4)
}

case class ConsumerRecord(consumer: String, time: DateTime = null)