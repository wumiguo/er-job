package org.wumiguo.erjob.io.configuration

import org.wumiguo.erjob.io.configuration.source.{FirstSource, SecondSource}

import scala.beans.BeanProperty

/**
 * @author levinliu
 *         Created on 2021/01/18
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
class JoinSourcePair {

  @BeanProperty var name: String = ""
  @BeanProperty var forcedRerun: Boolean = false
  @BeanProperty var processedWithFlow: String = ""
  @BeanProperty var keepRunStatOnPath: String = ""
  @BeanProperty var firstSource: FirstSource = new FirstSource()
  @BeanProperty var secondSource: SecondSource = new SecondSource()
  @BeanProperty var joinRules: Array[JoinFieldRules] = Array()
  @BeanProperty var joinResult: JoinResult = new JoinResult()

  override def toString: String = s"JoinSourcePair(name: $name, " +
    s"processedWithFlow: $processedWithFlow, " +
    s"keepRunStatOnPath: $keepRunStatOnPath, " +
    s"firstSource: $firstSource, " +
    s"secondSource: $secondSource, " +
    s"joinRules: ${joinRules.toList}, " +
    s"joinResult: $joinResult " +
    s")"
}
