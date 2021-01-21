package org.wumiguo.erjob.argsparser

import org.wumiguo.erjob.io.configuration.flow.FlowSetting
import org.wumiguo.erjob.io.exprlang.ExprLangEvaluator

/**
 * @author levinliu
 *         Created on 2021/1/19
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
object FlowSettingSupport {
  def conf2Args(flowSetting: FlowSetting): Array[String] = {
    var flowArgs = Array[String]()
    flowArgs :+= "flowType=" + flowSetting.getOptionValue("type")
    flowArgs :+= "optionSize=" + flowSetting.options.size
    flowSetting.options.zipWithIndex.foreach(
      x => flowArgs :+= "option" + x._2 + "=" + x._1.key + ":" + ExprLangEvaluator.eval(x._1.value)
    )
    flowArgs
  }
}
