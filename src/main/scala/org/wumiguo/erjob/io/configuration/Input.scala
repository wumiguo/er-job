package org.wumiguo.erjob.io.configuration

import scala.beans.BeanProperty

/**
 * @author levinliu
 *         Created on 2020/7/15
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
class Input {
  @BeanProperty var path: String = ""
  override def toString: String = s"Input(path: $path)"

}
