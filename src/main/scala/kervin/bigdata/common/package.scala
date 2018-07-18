package kervin.bigdata

import scala.io.Source

package object common {
  def use[C <: AutoCloseable, T](s: C)(op: C => T): T = {
    try {
      op(s)
    } finally {
      if (s != null) s.close()
    }
  }
}
