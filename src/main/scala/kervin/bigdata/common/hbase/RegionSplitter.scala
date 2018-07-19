package kervin.bigdata.common.hbase

import java.nio.charset.Charset

import org.apache.commons.lang3.StringUtils

object RegionSplitter {
  private val UTF8_ENCODING = "UTF-8"

  private val UTF8_CHARSET = Charset.forName(UTF8_ENCODING)

  def hexStringSplit(num: Int): Array[Array[Byte]] = {
    hexStringSplit(num, "00000000", "FFFFFFFF")
  }

  def hexStringSplit(num: Int, size: Int): Array[Array[Byte]] = {
    hexStringSplit(num, "0" * size, "F" * size)
  }

  def hexStringSplit(num: Int, start: String, stop: String): Array[Array[Byte]] = {
    //    val max = java.lang.Long.parseLong("F" * size, 16)
    val radix = 16
    val size = stop.length
    split(num, start, stop).map(x => {
      StringUtils.leftPad(x.toString(radix), size, '0').getBytes(UTF8_CHARSET)
    })
  }

  def split(num: Int, start: String, stop: String): Array[BigInt] = {
    val radix = 16
    val start_ = BigInt(start, radix)
    val stop_ = BigInt(stop, radix)
    split(num, start_, stop_)
  }

  def split(num: Int, start_ : BigInt, stop_ : BigInt): Array[BigInt] = {
    val range = start_ until(stop_, (stop_ - start_) / num)
    range.drop(1).toArray
  }

  def uniformSplit(num: Int, start: Array[Byte], stop: Array[Byte]): Array[Array[Byte]] = {
    val length = stop.length
    val start_ = if (start == null || start.isEmpty) BigInt(0) else BigInt(start)
    val stop_ = BigInt(Array.concat(Array(0.toByte), stop))
    split(num, start_, stop_).map {
      b =>
        val bytes = b.toByteArray
        val res = new Array[Byte](length)
        System.arraycopy(bytes, bytes.length - length, res, 0, length)
        res
    }
  }
}
