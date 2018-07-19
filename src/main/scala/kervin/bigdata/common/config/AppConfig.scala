package kervin.bigdata.common.config

import java.io.File

import com.typesafe.config.impl.ConfigImpl
import com.typesafe.config.{Config, ConfigFactory}
import kervin.bigdata.common.Logging
import pureconfig.{ConfigReader, Derivation}

import scala.reflect.ClassTag

trait AppConfig extends Logging {
  val configFile = "application.conf"
  private var config = ConfigFactory.load(configFile)

  def get: Config = config

  def merge(file: File): AppConfig = {
    if (file.exists() && file.isFile) {
      config = config.withFallback(ConfigFactory.parseFile(file))
    }
    this
  }

  /**
    * args : [ path=value , output=hello]
    *
    * @param args
    */
  def loadArgs(args: Array[String]) = {
    val kvConf = args.toIterator.map(x => x.split("=", 2)).filter(_.length == 2).map {
      case Array(k, v) => (k, v)
    }.toMap
  }

  def get(path: String): Option[String] =
    if (config.hasPath(path)) Some(config.getAnyRef(path).toString)
    else None

  def apply(path: String): String = get(path).get

  def update(path: String, value: String): Unit = {
    config = config.withValue(path,
      ConfigImpl.fromAnyRef(value, null)
    )
  }

  def put(path: String, value: String): Unit = {
    this (path) = value
  }

  def checkPath(paths: String*) {
    val err = paths.filterNot(config.hasPath)
    if (err.nonEmpty) {
      throw new IllegalArgumentException("缺失输入参数:" + err.mkString("[", ",", "]"))
    }
  }

  def loadConfigOrThrow[C: ClassTag](implicit reader: Derivation[ConfigReader[C]]): C = {
    pureconfig.loadConfigOrThrow[C](config)
  }

  def loadConfigOrThrow[C: ClassTag](namespace: String)(implicit reader: Derivation[ConfigReader[C]]): C = {
    pureconfig.loadConfigOrThrow[C](config, namespace)
  }
}

object AppConfig extends AppConfig
