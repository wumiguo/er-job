package org.wumiguo.erjob.argsparser

import org.scalatest.flatspec.AnyFlatSpec
import org.wumiguo.erjob.io.configuration.flow.{FlowSetting, FlowSettingOption}

/**
 * @author levinliu
 *         Created on 2021/1/21
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
class FlowSettingSupportTest extends AnyFlatSpec {
  it should "conf2args " in {
    val flowSetting = new FlowSetting()
    flowSetting.name = "DemoFlow"
    val opt1 = new FlowSettingOption()
    opt1.key = "type"
    opt1.value = "EDJoin"
    val opt2 = new FlowSettingOption()
    opt2.key = "q"
    opt2.value = 2
    flowSetting.options = Array(
      opt1, opt2
    )
    val args = FlowSettingSupport.conf2Args(flowSetting)
    assertResult(Seq("flowType=EDJoin", "optionSize=2", "option0=type:EDJoin", "option1=q:2"))(args.toSeq)
  }
}
