package org.wumiguo.erjob.io.configuration

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
  @BeanProperty var showSimilarity: Boolean = false

  override def toString: String = s"Output(path: $path, dataType: $dataType, " +
    s"overwriteOnExist: $overwriteOnExist, showSimilarity: $showSimilarity)"

}
