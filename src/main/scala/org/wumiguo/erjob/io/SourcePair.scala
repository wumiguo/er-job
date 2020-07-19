package org.wumiguo.erjob.io

import scala.beans.BeanProperty

/**
 * @author levinliu
 *         Created on 2020/7/15
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
class SourcePair {
  @BeanProperty var name: String = ""
  @BeanProperty var sourcePair: Array[String] = Array("")
  @BeanProperty var idFields: Array[String] = Array("")
  @BeanProperty var joinFields: Array[String] = Array("")
  @BeanProperty var statePath: String = ""
  @BeanProperty var forcedRun: Boolean = false
  @BeanProperty var joinResultFile: String = ""

  override def toString: String = s"name: $name," +
    s" statePath: $statePath," +
    s" forcedRun: $forcedRun," +
    s" joinResultFile: $joinResultFile," +
    s" sourcePair: ${sourcePair.toList}," +
    s" joinFields: ${joinFields.toList}, " +
    s" idFields: ${idFields.toList}"

}
