package org.wumiguo.erjob.io.configuration

import scala.beans.BeanProperty

/**
 * @author levinliu
 *         Created on 2020/7/20
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
class FlowSetting {
  def getOptionValue(key: String): Any = {
    options.filter(_.key == key).head.value
  }

  @BeanProperty var name: String = ""
  @BeanProperty var options: Array[FlowSettingOption] = Array()

  override def toString: String = s"FlowSetting(name: $name options: ${options.toList})"

}
