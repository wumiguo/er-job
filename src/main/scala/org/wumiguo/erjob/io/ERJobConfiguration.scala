package org.wumiguo.erjob.io

import scala.beans.BeanProperty

/**
 * @author levinliu
 *         Created on 2020/7/15
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
class ERJobConfiguration {
  @BeanProperty var dataType: String = ""
  @BeanProperty var sourcesPairs:List[SourcePair] = List()
}
