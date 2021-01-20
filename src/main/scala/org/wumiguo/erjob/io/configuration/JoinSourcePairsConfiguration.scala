package org.wumiguo.erjob.io.configuration

import scala.beans.BeanProperty

/**
 * @author levinliu
 *         Created on 2021/01/18
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
class JoinSourcePairsConfiguration {

  @BeanProperty var joinSourcePairs: Array[JoinSourcePair] = Array()

  override def toString: String = s"JoinSourcePairsConfiguration( " +
    s"joinSourcePairs: $joinSourcePairs " +
    s")"
}
