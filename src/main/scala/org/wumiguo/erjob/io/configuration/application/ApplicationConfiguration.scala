package org.wumiguo.erjob.io.configuration.application

import org.wumiguo.erjob.io.configuration.flow.FlowSetting

import scala.beans.BeanProperty
import scala.collection.mutable

/**
 * @author levinliu
 *         Created on 2020/9/3
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
class ApplicationConfiguration {
  def getSetting(key: String, defaultValue: String): String = {
    spark.find(_.key == key).getOrElse(option(key, defaultValue)).value
  }

  @BeanProperty var spark: Array[SettingOption] = Array()

  override def toString: String = s"ApplicationConfiguration(spark: ${spark.toList})"


  def option(key: String, value: String) = {
    val opt = new SettingOption
    opt.setKey(key)
    opt.setValue(value)
    opt
  }
}
