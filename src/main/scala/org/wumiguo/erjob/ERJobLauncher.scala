package org.wumiguo.erjob

import java.io.{File, FileInputStream}

import org.slf4j.LoggerFactory
import org.wumiguo.erjob.io.ERJobConfiguration
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor


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
    val yamlPath = "src/main/resources/er-job-configuration.yml"
    val input = new FileInputStream(new File(yamlPath))
    val yaml = new Yaml(new Constructor(classOf[ERJobConfiguration]))
    val e = yaml.load(input).asInstanceOf[ERJobConfiguration]
    log.info("job configuration is " + e)
    log.info("er job is completed")
  }
}
