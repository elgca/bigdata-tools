package kervin.bigdata.common.hbase

import kervin.bigdata.common.Logging
import kervin.bigdata.common.use
import org.apache.hadoop.hbase.client.{Admin, Connection, ConnectionFactory, Table}
import org.apache.hadoop.hbase.TableName

class HBaseTool extends AutoCloseable with Logging {
  private val connection: Connection = {
    ConnectionFactory.createConnection()
  }

  override def close(): Unit = ???

  implicit def name2Table(name: String) = TableName.valueOf(name)

  def useTable[T](name: String)(op: Table => T): T = {
    use(connection.getTable(name))(op)
  }

  def useAdmin[T](op: Admin => T): T = {
    use(connection.getAdmin)(op)
  }
}
