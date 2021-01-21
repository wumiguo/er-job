package org.wumiguo.erjob.argsparser

import org.scalatest.flatspec.AnyFlatSpec
import org.wumiguo.erjob.TestDirs
import org.wumiguo.erjob.io.JoinSourcePairsConfigurationLoader

/**
 * @author levinliu
 *         Created on 2021/1/21
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
class JoinSourcePairSupportTest extends AnyFlatSpec {
  it should "conf2args " in {
    val yamlPath = TestDirs.resolveTestResourcePath("sample/yml/join-source-pairs-configuration.yml")
    val erJobConf = JoinSourcePairsConfigurationLoader.load(yamlPath)
    assertResult(1)(erJobConf.joinSourcePairs.size)
    val joinSourcePair = erJobConf.joinSourcePairs(0)
    val args = JoinSourcePairSupport.conf2Args(joinSourcePair)
    assertResult(
      Seq("dataSet1=data-input/first.csv", "dataSet1-id=primary_id", "dataSet1-attrSet=a,a2",
        "dataSet1-additionalAttrSet=", "dataSet1-filterSize=2", "dataSet1-filter0=site:CN",
        "dataSet1-filter1=t_date:20210101,20210102",
        "dataSet2=data-input/second.csv", "dataSet2-id=", "dataSet2-attrSet=b,b2",
        "dataSet2-additionalAttrSet=b_sys,b_date", "dataSet2-filterSize=1", "dataSet2-filter0=t_date:20210101,20210102",
        "joinFieldsWeight=0.7,0.3", "outputPath=data-output/first-second", "outputType=csv",
        "joinResultFile=mapping", "overwriteOnExist=true", "showSimilarity=true",
        "connectedClustering=false")
    )(args.toSeq)
  }
}
