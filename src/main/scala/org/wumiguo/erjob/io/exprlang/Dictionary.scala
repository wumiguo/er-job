package org.wumiguo.erjob.io.exprlang

import java.text.SimpleDateFormat
import java.util.Date

import scala.collection.mutable

/**
 * @author levinliu
 *         Created on 2021/1/21
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
object Dictionary {
  val map: Map[String, Any] = Map(
    "$today" -> new SimpleDateFormat("YYYYMMDD").format(new Date())
  )

  def lookup(label: String) = {
    map.get(label).getOrElse(label).toString
  }
}
