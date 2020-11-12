package org.wumiguo.erjob

import java.io.File

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.wumiguo.erjob.io.configuration.flow.FlowSetting
import org.wumiguo.erjob.io.configuration.{Input, Output, SourcePair}
import org.wumiguo.erjob.io.{ERJobConfigurationLoader, FlowsConfigurationLoader}
import org.wumiguo.ser.ERFlowLauncher
import org.wumiguo.ser.common.{SparkAppConfiguration, SparkAppConfigurationSupport, SparkEnvSetup}
import org.wumiguo.ser.methods.util.CommandLineUtil


/**
 *
 * @author levinliu
 *         Created on 2020/7/15
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
object BaseJobLauncher extends SparkEnvSetup {

  def main(args: Array[String]): Unit = {
    log.info("start to run er job")
    val jobConfPath = CommandLineUtil.getParameter(args, "jobConfPath", "src/main/resources/er-job-configuration.yml")
    val erJobConf = ERJobConfigurationLoader.load(jobConfPath)
    log.info("job configuration is " + erJobConf + " from path " + jobConfPath)
    val input = erJobConf.getInput
    val output = erJobConf.getOutput
    val sourcePairs = erJobConf.getSourcesPairs
    var sourceCounter = 0
    val sparkConf = SparkAppConfigurationSupport.args2SparkConf(args)
    val spark = createSparkSession("base-job-launcher", appConf = sparkConf)
    var statPathArr = Array[String]()
    val flowConfPath = CommandLineUtil.getParameter(args, "flowConfPath", "src/main/resources/flows-configuration.yml")
    val flowsConf = FlowsConfigurationLoader.load(flowConfPath)
    log.info("flowsConf=" + flowsConf + " from path " + flowConfPath)
    val flowSetting = flowsConf.lookupFlow(erJobConf.getUseFlow).get
    for (sp <- sourcePairs) {
      val statePath = output.path + "/" + sp.statePath
      statPathArr :+= statePath
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
            val epPath1 = input.path + "/" + sp.sourcePair(0)
            val epPath2 = input.path + "/" + sp.sourcePair(1)
            if (!fs.exists(new org.apache.hadoop.fs.Path(epPath1))) {
              throw new RuntimeException("Fail to resolve the data source from path " + epPath1)
            }
            if (!fs.exists(new org.apache.hadoop.fs.Path(epPath2))) {
              throw new RuntimeException("Fail to resolve the data source from path " + epPath2)
            }
            callERFlowLauncher(sparkConf, input, output, sp, epPath1, epPath2, flowSetting)
            persistStat(output, spark, sp, statePath)
          } else {
            log.info("skip process on source pair:" + sp)
          }
        }
      } else {
        log.info("start to use ER flow to process data")
        sourceCounter += 1
        val epPath1 = input.path + "/" + sp.sourcePair(0)
        val epPath2 = input.path + "/" + sp.sourcePair(1)
        if (!fs.exists(new org.apache.hadoop.fs.Path(epPath1))) {
          throw new RuntimeException("Fail to resolve the data source from path " + epPath1)
        }
        if (!fs.exists(new org.apache.hadoop.fs.Path(epPath2))) {
          throw new RuntimeException("Fail to resolve the data source from path " + epPath2)
        }
        callERFlowLauncher(sparkConf, input, output, sp, epPath1, epPath2, flowSetting)
        persistStat(output, spark, sp, statePath)
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

  private def persistStat(output: Output, spark: SparkSession, sp: SourcePair, statePath: String) = {
    import spark.implicits._
    val statRdd = spark.sparkContext.makeRDD(Seq("SUCCESS", output.path + "/" + sp.joinResultFile + "-" + output.dataType), 4)
    statRdd.toDF.write.mode(SaveMode.Overwrite).text(statePath)
  }

  private def callERFlowLauncher(sparkConf: SparkAppConfiguration, input: Input, output: Output, sp: SourcePair, epPath1: String, epPath2: String, flowSetting: FlowSetting) = {
    var flowArgs = Array[String]()
    flowArgs ++= SparkAppConfigurationSupport.sparkConf2Args(sparkConf)
    flowArgs :+= "flowType=" + flowSetting.getOptionValue("type")
    flowArgs :+= "dataSet1=" + epPath1
    val id1 = if (sp.idFields.size >= 1) {
      sp.idFields(0)
    } else {
      ""
    }
    flowArgs :+= "dataSet1-id=" + id1
    flowArgs :+= "dataSet1-attrSet=" + sp.joinFields.map(_.source1Field).reduce(_ + "," + _)
    val addFields1 = if (sp.source1AdditionalExtractFields.isEmpty) {
      ""
    } else {
      sp.source1AdditionalExtractFields.reduce(_ + "," + _)
    }
    flowArgs :+= "dataSet1-additionalAttrSet=" + addFields1

    flowArgs :+= "dataSet1-filterSize=" + sp.source1Filters.size
    sp.source1Filters.zipWithIndex.foreach(x => flowArgs :+= "dataSet1-filter" + x._2 + "=" + x._1.field + ":" + x._1.values.mkString(","))
    flowArgs :+= "dataSet2=" + epPath2
    val id2 = if (sp.idFields.size >= 2) {
      sp.idFields(1)
    } else {
      ""
    }
    flowArgs :+= "dataSet2-id=" + id2
    flowArgs :+= "dataSet2-attrSet=" + sp.joinFields.map(_.source2Field).reduce(_ + "," + _)
    val addFields2 = if (sp.source2AdditionalExtractFields.isEmpty) {
      ""
    } else {
      sp.source2AdditionalExtractFields.reduce(_ + "," + _)
    }
    flowArgs :+= "dataSet2-additionalAttrSet=" + addFields2
    flowArgs :+= "dataSet2-filterSize=" + sp.source2Filters.size
    sp.source2Filters.zipWithIndex.foreach(x => flowArgs :+= "dataSet2-filter" + x._2 + "=" + x._1.field + ":" + x._1.values.mkString(","))
    flowArgs :+= "joinFieldsWeight=" + sp.joinFields.map(_.weight.toString).reduce(_ + "," + _)
    flowArgs :+= "optionSize=" + flowSetting.options.size
    flowSetting.options.zipWithIndex.foreach(
      x => flowArgs :+= "option" + x._2 + "=" + x._1.key + ":" + x._1.value
    )
    flowArgs :+= "outputPath=" + output.getPath
    flowArgs :+= "outputType=" + output.getDataType
    flowArgs :+= "joinResultFile=" + sp.joinResultFile
    flowArgs :+= "overwriteOnExist=" + output.getOverwriteOnExist
    flowArgs :+= "showSimilarity=" + output.getShowSimilarity
    log.info("flowArgs=" + flowArgs.toList)
    ERFlowLauncher.main(flowArgs)
  }
}
