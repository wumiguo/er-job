package org.wumiguo.erjob.io

import scala.beans.BeanProperty

/**
 * @author levinliu
 *         Created on 2020/7/15
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
class Input {
  @BeanProperty var path: String = ""
  @BeanProperty var dataType: String = ""
}
