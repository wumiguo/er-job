package org.wumiguo.erjob.io.configuration

import scala.beans.BeanProperty

/**
 * @author levinliu
 *         Created on 2020/7/20
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
class FlowsConfiguration {
  def lookupFlow(flow: String): FlowSetting = {
    getFlows.filter(_.name == flow).head
  }

  @BeanProperty var flows: Array[FlowSetting] = Array()

  override def toString: String = s"flows: ${flows.toList}"

}
