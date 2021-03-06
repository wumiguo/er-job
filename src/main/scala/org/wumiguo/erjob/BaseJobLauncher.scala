package org.wumiguo.erjob

import java.util.concurrent.{Callable, Executors, Future}

import org.apache.hadoop.fs.FileSystem
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.wumiguo.erjob.argsparser.{FlowSettingSupport, SourcePairSupport}
import org.wumiguo.erjob.io.configuration.flow.FlowSetting
import org.wumiguo.erjob.io.configuration.{Input, Output, SourcePair}
import org.wumiguo.erjob.io.{ERJobConfigurationLoader, FlowsConfigurationLoader}
import org.wumiguo.ser.ERFlowLauncher
import org.wumiguo.ser.common.{SparkAppConfiguration, SparkAppConfigurationSupport, SparkEnvSetup}
import org.wumiguo.ser.methods.util.CommandLineUtil

import scala.collection.mutable


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
    val executors = Executors.newFixedThreadPool(sourcePairs.size)
    val taskList = mutable.MutableList[Future[(String, Int)]]()
    for (sp <- sourcePairs) {
      val task = executors.submit(new Callable[(String, Int)] {
        override def call(): (String, Int) = {
          log.info("start to run job basing on source pair " + sp)
          val result = handleSourcePair(input, output, sparkConf, spark, flowSetting, sp)
          log.info("finish job basing on source pair " + sp)
          result
        }
      })
      taskList += task
    }
    taskList.foreach(result => {
      val output = result.get
      statPathArr :+= output._1
      sourceCounter += output._2
    })
    executors.shutdown()
    if (sourceCounter == 0) {
      log.info("No mapping file generated")
      return
    }
    //Join all generated mapping into an united
    // MappingJoinHandler.join(statPathArr, sparkSession)
    //    callERFlowLauncher(Input(),Output(),,mapping1Path,mapping2Path)
  }

  private def handleSourcePair(input: Input, output: Output, sparkConf: SparkAppConfiguration, spark: SparkSession, flowSetting: FlowSetting, sp: SourcePair) = {
    val statePath = output.path + "/" + sp.statePath
    val conf = spark.sparkContext.hadoopConfiguration
    val fs = org.apache.hadoop.fs.FileSystem.get(conf)
    val exists = fs.exists(new org.apache.hadoop.fs.Path(statePath))
    log.info("path " + statePath + " exist " + exists)
    if (exists && !sp.forcedRun) {
      val state = spark.sparkContext.textFile(statePath)
      if (state.count() == 0) {
        log.info("error on exist")
        throw new RuntimeException("Error on stat file from path " + statePath)
      } else {
        val first = state.first().toString
        log.info("state content is =" + first)
        if (first != "SUCCESS") {
          log.info("start to use ER flow to process data")
          preCheckOnSourcePath(input, sp, fs)
          callERFlowLauncher(sparkConf, input, output, sp, flowSetting)
          persistStat(output, spark, sp, statePath)
          (statePath, 1)
        } else {
          log.info("skip process on source pair:" + sp)
          (statePath, 0)
        }
      }
    } else {
      log.info("start to use ER flow to process data")
      preCheckOnSourcePath(input, sp, fs)
      callERFlowLauncher(sparkConf, input, output, sp, flowSetting)
      persistStat(output, spark, sp, statePath)
      (statePath, 1)
    }
  }


  private def preCheckOnSourcePath(input: Input, sp: SourcePair, fs: FileSystem) = {
    val epPath1 = input.path + "/" + sp.sourcePair(0)
    if (!fs.exists(new org.apache.hadoop.fs.Path(epPath1))) {
      throw new RuntimeException("Fail to resolve the data source from path " + epPath1)
    }
    val epPath2 = input.path + "/" + sp.sourcePair(1)
    if (!fs.exists(new org.apache.hadoop.fs.Path(epPath2))) {
      throw new RuntimeException("Fail to resolve the data source from path " + epPath2)
    }
  }

  private def persistStat(output: Output, spark: SparkSession, sp: SourcePair, statePath: String) = {
    import spark.implicits._
    val statRdd = spark.sparkContext.makeRDD(Seq("SUCCESS", output.path + "/" + sp.joinResultFile + "-" + output.dataType), 4)
    statRdd.toDF.write.mode(SaveMode.Overwrite).text(statePath)
  }

  private def callERFlowLauncher(sparkConf: SparkAppConfiguration, input: Input, output: Output, sp: SourcePair, flowSetting: FlowSetting) = {
    var flowArgs: Array[String] = SourcePairSupport.conf2Args(input, output, sp)
    flowArgs ++= SparkAppConfigurationSupport.sparkConf2Args(sparkConf)
    flowArgs ++= FlowSettingSupport.conf2Args(flowSetting)
    log.info("flowArgs=" + flowArgs.toList)
    ERFlowLauncher.main(flowArgs)
  }

}
