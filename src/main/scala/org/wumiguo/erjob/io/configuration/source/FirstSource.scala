package org.wumiguo.erjob.io.configuration.source

import org.wumiguo.erjob.io.configuration.SourceFilterOption

import scala.beans.BeanProperty

/**
 * @author levinliu
 *         Created on 2021/1/18
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
class FirstSource {
  @BeanProperty var name: String = ""
  @BeanProperty var idField: String = ""
  @BeanProperty var loadDataFromPath: String = ""
  @BeanProperty var filterOnFields: Array[SourceFilterOption] = Array()

  override def toString: String = s"FirstSource( " +
    s"name: $name, " +
    s"idField: $idField, " +
    s"loadDataFromPath: $loadDataFromPath, " +
    s"filterOnFields: ${filterOnFields.toList} " +
    s")"
}
