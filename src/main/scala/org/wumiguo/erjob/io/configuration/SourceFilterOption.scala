package org.wumiguo.erjob.io.configuration

import scala.beans.BeanProperty

/**
 * @author levinliu
 *         Created on 2020/8/19
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
class SourceFilterOption {
  @BeanProperty var field: String = ""
  @BeanProperty var value: String = ""

  override def toString: String = s"SourceFilterOption(field: $field, value: $value)"
}
