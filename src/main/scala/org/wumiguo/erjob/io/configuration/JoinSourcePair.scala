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
  @BeanProperty var disable: Boolean = false
  @BeanProperty var runEvenRunStatExist: Boolean = false
  @BeanProperty var processedWithFlow: String = ""
  @BeanProperty var preserveRunStatOnPath: String = ""
  @BeanProperty var firstSource: FirstSource = new FirstSource()
  @BeanProperty var secondSource: SecondSource = new SecondSource()
  @BeanProperty var joinRules: Array[JoinFieldRules] = Array()
  @BeanProperty var joinResult: JoinResult = new JoinResult()

  override def toString: String = s"JoinSourcePair(name: $name, " +
    s"processedWithFlow: $processedWithFlow, " +
    s"disable: $disable, " +
    s"runEvenRunStatExist: $runEvenRunStatExist, " +
    s"preserveRunStatOnPath: $preserveRunStatOnPath, " +
    s"firstSource: $firstSource, " +
    s"secondSource: $secondSource, " +
    s"joinRules: ${joinRules.toList}, " +
    s"joinResult: $joinResult " +
    s")"
}
