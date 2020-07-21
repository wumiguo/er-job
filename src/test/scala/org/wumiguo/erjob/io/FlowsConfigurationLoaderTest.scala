package org.wumiguo.erjob.io

import org.scalatest.flatspec.AnyFlatSpec
import org.wumiguo.erjob.TestDirs

/**
 * @author levinliu
 *         Created on 2020/7/21
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
class FlowsConfigurationLoaderTest extends AnyFlatSpec {
  it should "load flows config" in {
    val yamlPath = TestDirs.resolveTestResourcePath("sample/yml/flows-sample.yml")
    val flowsConf = FlowsConfigurationLoader.load(yamlPath)
    assertResult(1)(flowsConf.flows.size)
    assertResult(None)(flowsConf.lookupFlow("AA"))
    assertResult("EdJoin")(flowsConf.lookupFlow("EdJoin").get.name)
  }
}
