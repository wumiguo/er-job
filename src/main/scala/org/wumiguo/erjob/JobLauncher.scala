package org.wumiguo.erjob

import java.io.File

import org.apache.spark.sql.SaveMode
import org.wumiguo.erjob.io.configuration.{FlowSetting, Input, Output, SourcePair}
import org.wumiguo.erjob.io.{ERJobConfigurationLoader, FlowsConfigurationLoader}
import org.wumiguo.ser.ERFlowLauncher
import org.wumiguo.ser.common.SparkEnvSetup
import org.wumiguo.ser.methods.util.CommandLineUtil


/**
 * @author levinliu
 *         Created on 2020/7/15
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
object JobLauncher extends SparkEnvSetup {

  def main(args: Array[String]): Unit = {
    log.info("start to run er job")
    val jobConfPath = CommandLineUtil.getParameter(args, "jobConfPath", "src/main/resources/er-job-configuration.yml")
    val erJobConf = ERJobConfigurationLoader.load(jobConfPath)
    log.info("job configuration is " + erJobConf + " from path " + jobConfPath)
    val input = erJobConf.getInput
    val output = erJobConf.getOutput
    val sourcePairs = erJobConf.getSourcesPairs
    var sourceCounter = 0
    val outputDir = new File("/tmp/data-er")
    if (!outputDir.exists()) {
      outputDir.mkdirs()
    }
    val spark = createLocalSparkSession(getClass.getName, outputDir = output.path)
    var statPathArr = Array[String]()
    val flowConfPath = CommandLineUtil.getParameter(args, "flowConfPath", "src/main/resources/flows-configuration.yml")
    val flowsConf = FlowsConfigurationLoader.load(flowConfPath)
    log.info("flowsConf=" + flowsConf + " from path " + flowConfPath)
    val flowSetting = flowsConf.lookupFlow(erJobConf.getUseFlow).get
    for (sp <- sourcePairs) {
      val statePath = output.path + "/" + sp.statePath
      statPathArr +:= statePath
      val conf = spark.sparkContext.hadoopConfiguration
      val fs = org.apache.hadoop.fs.FileSystem.get(conf)
      val exists = fs.exists(new org.apache.hadoop.fs.Path(statePath))
      log.info("path " + statePath + " exist " + exists)
      if (exists && !sp.forcedRun) {
        val state = spark.sparkContext.textFile(statePath)
        if (state.count() == 0) {
          log.info("error on exist")
          System.exit(0)
        } else {
          val first = state.first().toString
          log.info("state content is =" + first)
          if (first != "SUCCESS") {
            log.info("start to use ER flow to process data")
            sourceCounter += 1
            val epPath1 = input.path + "/" + sp.sourcePair(0) + "." + input.getDataType
            val epPath2 = input.path + "/" + sp.sourcePair(1) + "." + input.getDataType
            if (!new File(epPath1).exists || !new File(epPath2).exists) {
              throw new RuntimeException("Fail to resolve the data source from path " + epPath1 + " and " + epPath2)
            }
            callERFlowLauncher(input, output, sp, epPath1, epPath2, flowSetting)
            import spark.implicits._
            val statRdd = spark.sparkContext.makeRDD(Seq("SUCCESS", output.path + "/" + sp.joinResultFile + "-" + output.dataType), 4)
            statRdd.toDF.write.mode(SaveMode.Overwrite).text(statePath)
          } else {
            log.info("skip process on source pair:" + sp)
          }
        }
      } else {
        log.info("start to use ER flow to process data")
        sourceCounter += 1
        val epPath1 = input.path + "/" + sp.sourcePair(0) + "." + input.getDataType
        val epPath2 = input.path + "/" + sp.sourcePair(1) + "." + input.getDataType
        if (!new File(epPath1).exists || !new File(epPath2).exists) {
          throw new RuntimeException("Fail to resolve the data source from path " + epPath1 + " and " + epPath2)
        }
        callERFlowLauncher(input, output, sp, epPath1, epPath2, flowSetting)
        import spark.implicits._
        val statRdd = spark.sparkContext.makeRDD(Seq("SUCCESS", output.path + "/" + sp.joinResultFile + "-" + output.dataType), 4)
        statRdd.toDF.write.mode(SaveMode.Overwrite).text(statePath)
      }
    }
    if (statPathArr.size == 0) {
      log.info("No mapping file generated")
      return
    }
    //Join all generated mapping into an united
    // MappingJoinHandler.join(statPathArr, sparkSession)
    //    callERFlowLauncher(Input(),Output(),,mapping1Path,mapping2Path)
  }

  private def callERFlowLauncher(input: Input, output: Output, sp: SourcePair, epPath1: String, epPath2: String, flowSetting: FlowSetting) = {
    var flowArgs = Array[String]()
    flowArgs +:= "flowType=" + flowSetting.getOptionValue("type")
    flowArgs +:= "dataSet1=" + epPath1
    flowArgs +:= "dataSet1-id=" + sp.idFields(0)
    flowArgs +:= "dataSet1-format=" + input.getDataType
    flowArgs +:= "dataSet1-attrSet=" + sp.joinFields(0)
    flowArgs +:= "dataSet2=" + epPath2
    flowArgs +:= "dataSet2-id=" + sp.idFields(1)
    flowArgs +:= "dataSet2-format=" + input.getDataType
    flowArgs +:= "dataSet2-attrSet=" + sp.joinFields(1)
    flowArgs +:= "optionSize=" + flowSetting.options.size
    flowSetting.options.zipWithIndex.foreach(
      x => flowArgs +:= "option" + x._2 + "=" + x._1.key + ":" + x._1.value
    )
    flowArgs +:= "outputPath=" + output.getPath
    flowArgs +:= "outputType=" + output.getDataType
    flowArgs +:= "joinResultFile=" + sp.joinResultFile
    flowArgs +:= "overwriteOnExist=" + output.getOverwriteOnExist
    log.info("flowArgs=" + flowArgs.toList)
    ERFlowLauncher.main(flowArgs)
  }
}
