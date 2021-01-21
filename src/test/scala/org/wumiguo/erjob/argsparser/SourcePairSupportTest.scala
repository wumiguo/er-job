package org.wumiguo.erjob.argsparser

import org.scalatest.flatspec.AnyFlatSpec
import org.wumiguo.erjob.TestDirs
import org.wumiguo.erjob.io.{ERJobConfigurationLoader, JoinSourcePairsConfigurationLoader}

/**
 * @author levinliu
 *         Created on 2021/1/21
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
class SourcePairSupportTest extends AnyFlatSpec {

  it should "conf2args " in {
    val yamlPath = TestDirs.resolveTestResourcePath("sample/yml/er-job-configuration.yml")
    val erJobConf = ERJobConfigurationLoader.load(yamlPath)
    assertResult(2)(erJobConf.sourcesPairs.size)
    val sourcePair = erJobConf.sourcesPairs(0)
    val args = SourcePairSupport.conf2Args(erJobConf.input, erJobConf.output, sourcePair)
    assertResult(
      Seq("dataSet1=data-er-input/trade.csv", "dataSet1-id=t_id", "dataSet1-attrSet=t_pid",
        "dataSet1-additionalAttrSet=t_user,site", "dataSet1-filterSize=2", "dataSet1-filter0=site:CN",
        "dataSet1-filter1=t_date:20200715", "dataSet2=data-er-input/product.csv", "dataSet2-id=p_id",
        "dataSet2-attrSet=p_id", "dataSet2-additionalAttrSet=p_name,remark", "dataSet2-filterSize=1",
        "dataSet2-filter0=type:fund", "joinFieldsWeight=1.0", "outputPath=/tmp/data-er/",
        "outputType=csv", "joinResultFile=trade-product/tp-mapping", "overwriteOnExist=true",
        "showSimilarity=true", "connectedClustering=false"))(args.toSeq)
  }
  it should "V1 conf2args vs V2 conf2args " in {
    val yamlPath = TestDirs.resolveTestResourcePath("sample/yml/er-job-configuration.yml")
    val erJobConf = ERJobConfigurationLoader.load(yamlPath)
    assertResult(2)(erJobConf.sourcesPairs.size)
    val sourcePair = erJobConf.sourcesPairs(0)
    val args = SourcePairSupport.conf2Args(erJobConf.input, erJobConf.output, sourcePair)
    val yamlPath2 = TestDirs.resolveTestResourcePath("sample/yml/er-job-configuration-converted.yml")
    val convertedConf = JoinSourcePairsConfigurationLoader.load(yamlPath2)
    assertResult(2)(convertedConf.joinSourcePairs.size)
    val joinSourcePair = convertedConf.joinSourcePairs(0)
    val args2 = JoinSourcePairSupport.conf2Args(joinSourcePair)
    assertResult(args.toSeq.size)(args2.toSeq.size) //not exactly matched, but size should be same
  }
}
