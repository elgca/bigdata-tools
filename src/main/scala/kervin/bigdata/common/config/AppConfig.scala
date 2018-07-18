package kervin.bigdata.common.config

import kervin.bigdata.common.Logging

trait AppConfig extends Logging {
  val configFile = "application.conf"

  def loadArgs(args: Array[String]) = {
    val kvConf = args.toIterator.map(x => x.split("=", 2)).filter(_.length == 2).map {
      case Array(k, v) => (k, v)
    }.toMap
  }

  def get(path:String):Option[String] = None
}

object AppConfig extends AppConfig
