package org.wumiguo.erjob.io.configuration.flow

import scala.beans.BeanProperty

/**
 * @author levinliu
 *         Created on 2020/7/21
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
class FlowSettingOption {
  def option(key: String, value: Any):FlowSettingOption = {
    val opt = new FlowSettingOption
    opt.setKey(key)
    opt.setValue(value)
    opt
  }

  @BeanProperty var key: String = ""
  @BeanProperty var value: Any = Nil

  override def toString: String = s"FlowSettingOption(key: $key value: $value)"

}
