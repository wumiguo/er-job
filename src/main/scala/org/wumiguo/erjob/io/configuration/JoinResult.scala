package org.wumiguo.erjob.io.configuration

import scala.beans.BeanProperty

/**
 * @author levinliu
 *         Created on 2021/1/19
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
class JoinResult {
  @BeanProperty var savedResultOnPath: String = ""
  @BeanProperty var showSimilarity: Boolean = false
  @BeanProperty var connectedClustering: Boolean = false
  @BeanProperty var overwriteOnExist: Boolean = false

  override def toString: String = s"JoinResult(" +
    s"savedResultOnPath: $savedResultOnPath," +
    s"showSimilarity: $showSimilarity," +
    s"overwriteOnExist: $overwriteOnExist," +
    s"connectedClustering:$connectedClustering" +
    s" )"

}
