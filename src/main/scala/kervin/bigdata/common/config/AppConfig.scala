package kervin.bigdata.common.config

import kervin.bigdata.common.Logging

trait AppConfig extends Logging {
  def loadArgs(args: Array[String]): List[String] = {
    Nil
  }
}

object AppConfig extends AppConfig
