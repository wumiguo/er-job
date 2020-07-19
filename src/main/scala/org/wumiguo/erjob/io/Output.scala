package org.wumiguo.erjob.io

import scala.beans.BeanProperty

/**
 * @author levinliu
 *         Created on 2020/7/15
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
class Output {
  @BeanProperty var path: String = ""
  @BeanProperty var dataType: String = ""
  @BeanProperty var overwriteOnExist: Boolean = false

  override def toString: String = s"path: $path, dataType: $dataType "

}
