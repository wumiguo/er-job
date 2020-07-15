package org.wumiguo.erjob

import org.slf4j.LoggerFactory


/**
 * @author levinliu
 *         Created on 2020/7/15
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
object ERJobLauncher {
  val log = LoggerFactory.getLogger(getClass.getName)

  def main(args: Array[String]): Unit = {
    log.info("start to run er job")
    println("run er job now")

    log.info("er job is completed")
  }
}
