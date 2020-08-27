package org.wumiguo.erjob.mappinghandler

import org.apache.spark.sql.SparkSession
import org.slf4j.LoggerFactory

/**
 * @author levinliu
 *         Created on 2020/7/20
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
object MappingJoinHandler {
  val log = LoggerFactory.getLogger(getClass.getName)

  def join(statPathArr: Array[String], sparkSession: SparkSession) = {
    val mapping1 = sparkSession.read.text(statPathArr(0)).rdd
    mapping1.foreach(x => log.info("mapping1=" + x))
    val mapping1Path = mapping1.filter(_ != "SUCCESS").collect()(0).getAs[String](0)
    val mapping2 = sparkSession.read.text(statPathArr(1)).rdd
    mapping2.foreach(x => log.info("mapping2=" + x))
    val mapping2Path = mapping2.filter(_ != "SUCCESS").collect()(0).getAs[String](0)
    log.info("mapping path=" + mapping1Path)
    val d = sparkSession.read.csv(mapping1Path).rdd
    d.foreach(x => println("mapping1 entry=" + x))
    val d2 = sparkSession.read.csv(mapping2Path).rdd
    d2.foreach(x => println("mapping2 entry=" + x))
    log.info("er job is completed" + d)
  }
}
