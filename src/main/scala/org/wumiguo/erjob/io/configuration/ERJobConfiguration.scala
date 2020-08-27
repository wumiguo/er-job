package org.wumiguo.erjob.io.configuration

import scala.beans.BeanProperty

/**
 * @author levinliu
 *         Created on 2020/7/15
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
class ERJobConfiguration {

  @BeanProperty var input: Input = new Input()
  @BeanProperty var output: Output = new Output()
  @BeanProperty var sourcesPairs: Array[SourcePair] = Array()
  @BeanProperty var useFlow: String = ""

  override def toString: String = s"ERJobConfiguration(input: $input, " +
    s"output: $output, " +
    s"useFlow: $useFlow, " +
    s"sourcesPairs: ${sourcesPairs.toList}" +
    s")"
}
