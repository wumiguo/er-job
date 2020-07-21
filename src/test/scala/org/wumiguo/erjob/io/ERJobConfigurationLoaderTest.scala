package org.wumiguo.erjob.io

import org.scalatest.flatspec.AnyFlatSpec
import org.wumiguo.erjob.TestDirs

/**
 * @author levinliu
 *         Created on 2020/7/21
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
class ERJobConfigurationLoaderTest extends AnyFlatSpec {
  it should "load er job configuration" in {
    val yamlPath = TestDirs.resolveTestResourcePath("sample/yml/er-job-configuration.yml")
    val erJobConf = ERJobConfigurationLoader.load(yamlPath)
    assertResult("csv")(erJobConf.input.dataType)
    assertResult("csv")(erJobConf.output.dataType)
    assertResult("SampleFLow")(erJobConf.useFlow)
    assertResult(1)(erJobConf.sourcesPairs.size)
    assertResult("Test Join")(erJobConf.sourcesPairs(0).name)
  }
}
