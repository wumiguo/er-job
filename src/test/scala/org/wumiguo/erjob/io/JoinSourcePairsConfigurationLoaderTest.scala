package org.wumiguo.erjob.io

import org.scalatest.flatspec.AnyFlatSpec
import org.wumiguo.erjob.TestDirs
import org.wumiguo.erjob.io.configuration.SourceFilterOption

/**
 * @author levinliu
 *         Created on 2021/01/18
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
class JoinSourcePairsConfigurationLoaderTest extends AnyFlatSpec {
  it should "load join source pairs configuration" in {
    val yamlPath = TestDirs.resolveTestResourcePath("sample/yml/join-source-pairs-configuration.yml")
    val erJobConf = JoinSourcePairsConfigurationLoader.load(yamlPath)
    assertResult(1)(erJobConf.joinSourcePairs.size)
    val joinSourcePair = erJobConf.joinSourcePairs(0)
    assertResult("Test ER-JOIN")(joinSourcePair.name)
    assertResult(false)(joinSourcePair.disable)
    assertResult(true)(joinSourcePair.runEvenRunStatExist)
    assertResult("EDSimJoinFlow")(joinSourcePair.processedWithFlow)
    assertResult("data-output/a.txt")(joinSourcePair.preserveRunStatOnPath)
    val firstSource = joinSourcePair.firstSource;
    assertResult("first data source")(firstSource.name)
    assertResult("data-input/first.csv")(firstSource.loadDataFromPath)
    assertResult("site")(firstSource.filterOnFields(0).field)
    assertResult(Seq("CN"))(firstSource.filterOnFields(0).values.toSeq)
    assertResult("t_date")(firstSource.filterOnFields(1).field)
    assertResult(Seq("20210101", "20210102"))(firstSource.filterOnFields(1).values.toSeq)
    val secondSource = joinSourcePair.secondSource;
    assertResult("second data source")(secondSource.name)
    assertResult("data-input/second.csv")(secondSource.loadDataFromPath)
    assertResult("t_date")(secondSource.filterOnFields(0).field)
    assertResult(Seq("20210101", "20210102"))(secondSource.filterOnFields(0).values.toSeq)
    val joinRules = joinSourcePair.joinRules
    assertResult(2)(joinRules.size)
    assertResult("a")(joinRules(0).firstSourceField)
    assertResult("b")(joinRules(0).secondSourceField)
    assertResult(0.7)(joinRules(0).weight)
    assertResult("a2")(joinRules(1).firstSourceField)
    assertResult("b2")(joinRules(1).secondSourceField)
    assertResult(0.3)(joinRules(1).weight)
    val joinResult = joinSourcePair.joinResult
    assertResult(false)(joinResult.connectedClustering)
    assertResult(true)(joinResult.overwriteOnExist)
    assertResult(true)(joinResult.showSimilarity)
    assertResult("data-output/first-second/mapping.csv")(joinResult.savedResultOnPath)

  }
}
