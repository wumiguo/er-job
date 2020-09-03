package org.wumiguo.erjob.io.configuration.application

import scala.beans.BeanProperty

/**
 * @author levinliu
 *         Created on 2020/7/21
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
class SettingOption {

  @BeanProperty var key: String = ""
  @BeanProperty var value: String = ""

  override def toString: String = s"SettingOption(key: $key value: $value)"

}
