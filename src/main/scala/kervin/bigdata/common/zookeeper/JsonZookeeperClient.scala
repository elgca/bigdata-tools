package kervin.bigdata.common.zookeeper

import com.github.zkclient.ZkClient
import kervin.bigdata.common.json.JsonTools

import scala.language.implicitConversions

trait JsonZookeeperClient {

  implicit class JsonZkClient(zkClient: ZkClient) {
    @inline private implicit def str2Bytes(str: String) = str.getBytes("utf-8")

    @inline private implicit def bytes2Str(arr: Array[Byte]) = new String(arr)

    def writeJson[T <: AnyRef](path: String, obj: T): Unit = {
      val json = JsonTools.write(obj)
      zkClient.writeData(path, json)
    }

    def readJson[T](path: String)(implicit mf: Manifest[T]): T = {
      val json = zkClient.readData(path)
      JsonTools.read[T](json)
    }

    def readOrCreate[T <: AnyRef](path: String, default: => T)(implicit mf: Manifest[T]): T = {
      if (zkClient.exists(path)) {
        readJson[T](path)
      } else {
        zkClient.createPersistent(path, true)
        writeJson[T](path, default)
        default
      }
    }
  }

}
