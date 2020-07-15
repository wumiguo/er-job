package org.wumiguo.erjob

import java.util

import scala.beans.BeanProperty

/**
 * With the Snakeyaml Constructor approach shown in the main method,
 * this class must have a no-args constructor.
 * So case class is not supported as a snakeYaml model
 */
case class CaseSampleYml(title: String, date: String, owner: String, tags: util.ArrayList[String]) {
  override def toString: String = s"title: $title, owner: $owner, tags: $tags"
}