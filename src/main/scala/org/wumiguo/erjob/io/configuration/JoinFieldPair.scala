package org.wumiguo.erjob.io.configuration

import scala.beans.BeanProperty

/**
 * @author levinliu
 *         Created on 2020/8/19
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
class JoinFieldPair {
  @BeanProperty var source1Field: String = ""
  @BeanProperty var source2Field: String = ""
  @BeanProperty var weight: Float = 0

}
