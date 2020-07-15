package org.wumiguo.erjob

import scala.beans.BeanProperty

/**
 * With the Snakeyaml Constructor approach shown in the main method,
 * this class must have a no-args constructor.
 */
class SampleYml {
  @BeanProperty var title = ""
  @BeanProperty var date = ""
  @BeanProperty var owner = ""
  @BeanProperty var tags = new java.util.ArrayList[String]()

  override def toString: String = s"title: $title, date: $date, owner: $owner, tags: $tags"
}