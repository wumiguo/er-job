package org.wumiguo.erjob.io

import java.io.{File, FileInputStream}

import org.wumiguo.erjob.io.configuration.ERJobConfiguration
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

/**
 * @author levinliu
 *         Created on 2020/7/16
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
object ERJobConfigurationLoader {
  def load(yamlPath: String) = {
    val file = new FileInputStream(new File(yamlPath))
    val yaml = new Yaml(new Constructor(classOf[ERJobConfiguration]))
    val erJobConf = yaml.load(file).asInstanceOf[ERJobConfiguration]
    erJobConf
  }
}
