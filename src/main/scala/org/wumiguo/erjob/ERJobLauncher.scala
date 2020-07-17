package org.wumiguo.erjob

import java.io.{File, FileInputStream}

import org.slf4j.LoggerFactory
import org.wumiguo.erjob.io.{ERJobConfiguration, ERJobConfigurationLoader}
import org.wumiguo.ser.ERFlowLauncher
import org.wumiguo.ser.common.SparkEnvSetup
import org.wumiguo.ser.dataloader.{DataTypeResolver, ProfileLoaderFactory}
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor


/**
 * @author levinliu
 *         Created on 2020/7/15
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
object ERJobLauncher extends SparkEnvSetup {

  def main(args: Array[String]): Unit = {
    log.info("start to run er job")
    println("run er job now")
    val yamlPath = "src/main/resources/er-job-configuration.yml"
    val erJobConf = ERJobConfigurationLoader.load(yamlPath)
    log.info("job configuration is " + erJobConf)
    val input = erJobConf.getInput
    val output = erJobConf.getOutput
    val profileLoader = ProfileLoaderFactory.getDataLoader(DataTypeResolver.getDataType("." + input.dataType))
    val sourcePairs = erJobConf.getSourcesPairs
    var sourceCounter = 0
    val outputDir = new File("/tmp/data-er")
    if (!outputDir.exists()) {
      outputDir.mkdirs()
    }
    val spark = createLocalSparkSession(getClass.getName, outputDir = output.path)
    for (sp <- sourcePairs) {
      val epPath1 = input.path + "/" + sp.sourcePair(0) + "." + input.getDataType
      val epPath2 = input.path + "/" + sp.sourcePair(1) + "." + input.getDataType
      if (!new File(epPath1).exists || !new File(epPath2).exists) {
        throw new RuntimeException("Fail to resolve the data source from path " + epPath1 + " and " + epPath2)
      }
      var outputFinalPath = output.getPath
      if (output.namedWithSourcePair) {
        outputFinalPath += "/" + sp.sourcePair(0) + "-" + sp.sourcePair(1)
      }
      var flowArgs = Array[String]()
      flowArgs +:= "flowType=SSJoin"
      flowArgs +:= "dataSet1=" + epPath1
      flowArgs +:= "dataSet1-id=" + sp.idFields(0)
      flowArgs +:= "dataSet1-format=" + input.getDataType
      flowArgs +:= "dataSet1-attrSet=" + sp.joinFields(0)
      flowArgs +:= "dataSet2=" + epPath2
      flowArgs +:= "dataSet2-id=" + sp.idFields(1)
      flowArgs +:= "dataSet2-format=" + input.getDataType
      flowArgs +:= "dataSet2-attrSet=" + sp.joinFields(1)
      flowArgs +:= "q=2"
      flowArgs +:= "threshold=1"
        //set to 0 to make it exactly match
      flowArgs +:= "algorithm=EDJoin"
      flowArgs +:= "outputPath=" + outputFinalPath
      flowArgs +:= "outputType=" + output.getDataType
      ERFlowLauncher.main(flowArgs)
    }
    //Join all generated mapping into an united
    log.info("er job is completed")
  }
}
