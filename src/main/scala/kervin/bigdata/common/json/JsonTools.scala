package kervin.bigdata.common.json

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization

object JsonTools {
  implicit val formats: Formats = Serialization.formats(NoTypeHints)

  def read[T: Manifest](json: String): T = {
    Serialization.read[T](json)
  }

  def read[T: Manifest](json: JValue): T = {
    json.extract[T]
  }

  def write[A <: AnyRef](a: A): String = {
    Serialization.write(a)
  }

  def write(a: JValue): String = {
    compact(a)
  }
}
