package kervin.bigdata.common

import org.slf4j.{Logger, LoggerFactory}

trait Logging {
  val log: Logger = LoggerFactory.getLogger(this.getClass)
}
