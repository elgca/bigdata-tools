package kervin.bigdata.common

import kervin.bigdata.common.hbase.HBaseTool
import kervin.bigdata.common.zookeeper.JsonZookeeperClient

object Implicits extends JsonZookeeperClient with HBaseTool
