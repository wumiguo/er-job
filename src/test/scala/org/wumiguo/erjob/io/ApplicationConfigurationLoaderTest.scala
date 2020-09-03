package org.wumiguo.erjob.io

import org.scalatest.flatspec.AnyFlatSpec
import org.wumiguo.erjob.TestDirs

/**
 * @author levinliu
 *         Created on 2020/7/21
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
class ApplicationConfigurationLoaderTest extends AnyFlatSpec {
  it should "load app config" in {
    val yamlPath = TestDirs.resolveTestResourcePath("sample/yml/application.yml")
    val flowsConf = ApplicationConfigurationLoader.load(yamlPath)
    assertResult(1)(flowsConf.spark.size)
  }
}
