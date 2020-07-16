package org.wumiguo.erjob.io

import scala.beans.BeanProperty

/**
 * @author levinliu
 *         Created on 2020/7/15
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
class SourcePair {
  @BeanProperty var sourcePair: Array[String] = Array("")
  @BeanProperty var idFields: Array[String] = Array("")
  @BeanProperty var joinFields: Array[String] = Array("")

  override def toString: String = s"sourcePair: ${sourcePair.toList} , joinFields: ${joinFields.toList}, idFields: ${idFields.toList}"

}
