package org.wumiguo.erjob.io

import scala.beans.BeanProperty
import scala.collection.mutable

/**
 * @author levinliu
 *         Created on 2020/7/15
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
class ERJobConfiguration {

  @BeanProperty var input: Input = new Input()
  @BeanProperty var output: Input = new Input()
  @BeanProperty var sourcesPairs: Array[SourcePair] = Array()

  override def toString: String = s"input: $input, output: $output, sourcesPairs: ${sourcesPairs.toList}"
}
