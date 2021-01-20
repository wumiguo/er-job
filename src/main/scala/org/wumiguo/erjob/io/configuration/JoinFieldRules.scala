package org.wumiguo.erjob.io.configuration

import scala.beans.BeanProperty

/**
 * @author levinliu
 *         Created on 2021/01/18
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
class JoinFieldRules {
  @BeanProperty var firstSourceField: String = ""
  @BeanProperty var secondSourceField: String = ""
  @BeanProperty var weight: Double = 0.0

  override def toString: String = s"JoinFieldRules(" +
    s"firstSourceField: $firstSourceField," +
    s"secondSourceField: $secondSourceField," +
    s"weight:$weight )"
}
