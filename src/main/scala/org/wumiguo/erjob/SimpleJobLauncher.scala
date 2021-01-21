package org.wumiguo.erjob

import java.util.Date
import java.util.concurrent.{Callable, Executors, Future}

import org.apache.hadoop.fs.FileSystem
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.wumiguo.erjob.argsparser.{FlowSettingSupport, JoinSourcePairSupport}
import org.wumiguo.erjob.io.configuration.JoinSourcePair
import org.wumiguo.erjob.io.configuration.flow.{FlowSetting, FlowsConfiguration}
import org.wumiguo.erjob.io.{FlowsConfigurationLoader, JoinSourcePairsConfigurationLoader}
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
object SimpleJobLauncher extends SparkEnvSetup {

  def main(args: Array[String]): Unit = {
    log.info("start to run er job")
    val jobConfPath = CommandLineUtil.getParameter(args, "jobConfPath", "src/main/resources/er-job-configuration-v2.yml")
    val erJobConf = JoinSourcePairsConfigurationLoader.load(jobConfPath)
    log.info("job configuration is " + erJobConf + " from path " + jobConfPath)
    val sourcePairs = erJobConf.joinSourcePairs
    var sourceCounter = 0
    val sparkConf = SparkAppConfigurationSupport.args2SparkConf(args)
    val spark = createSparkSession("base-job-launcher", appConf = sparkConf)
    var statPathArr = Array[String]()
    val flowConfPath = CommandLineUtil.getParameter(args, "flowConfPath", "src/main/resources/flows-configuration.yml")
    val flowsConf = FlowsConfigurationLoader.load(flowConfPath)
    log.info("flowsConf=" + flowsConf + " from path " + flowConfPath)
    val executors = Executors.newFixedThreadPool(sourcePairs.size)
    val taskList = mutable.MutableList[Future[(String, Int)]]()
    for (sp <- sourcePairs) {
      val task = executors.submit(new Callable[(String, Int)] {
        override def call(): (String, Int) = {
          log.info("start to run job basing on source pair " + sp)
          val result = handleSourcePair(sparkConf, spark, sp, flowsConf)
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

  private def handleSourcePair(sparkConf: SparkAppConfiguration, spark: SparkSession, sp: JoinSourcePair, flowsConf: FlowsConfiguration) = {
    if (sp.disable) {
      (Nil, 0)
    }
    val flowSetting = flowsConf.lookupFlow(sp.processedWithFlow).get
    val statePath = sp.preserveRunStatOnPath
    val conf = spark.sparkContext.hadoopConfiguration
    val fs = org.apache.hadoop.fs.FileSystem.get(conf)
    val exists = fs.exists(new org.apache.hadoop.fs.Path(statePath))
    log.info("path " + statePath + " exist " + exists)
    val epPath1 = sp.firstSource.loadDataFromPath
    val epPath2 = sp.secondSource.loadDataFromPath
    if (exists && !sp.runEvenRunStatExist) {
      val state = spark.sparkContext.textFile(statePath)
      if (state.count() == 0) {
        log.info("error on exist")
        throw new RuntimeException("Error on stat file from path " + statePath)
      } else {
        val first = state.first().toString
        log.info("state content is =" + first)
        if (first != "SUCCESS") {
          log.info("start to use ER flow to process data")
          preCheckOnDataPath(fs, epPath1, epPath2)
          callERFlowLauncher(sparkConf, sp, flowSetting)
          persistStat(spark, sp.joinResult.savedResultOnPath, statePath)
          (statePath, 1)
        } else {
          log.info("skip process on source pair:" + sp)
          (statePath, 0)
        }
      }
    } else {
      log.info("start to use ER flow to process data")
      preCheckOnDataPath(fs, epPath1, epPath2)
      callERFlowLauncher(sparkConf, sp, flowSetting)
      persistStat(spark, sp.joinResult.savedResultOnPath, statePath)
      (statePath, 1)
    }
  }

  private def preCheckOnDataPath(fs: FileSystem, epPath1: String, epPath2: String) = {
    if (!fs.exists(new org.apache.hadoop.fs.Path(epPath1))) {
      throw new RuntimeException("Fail to resolve the data source from path " + epPath1)
    }
    if (!fs.exists(new org.apache.hadoop.fs.Path(epPath2))) {
      throw new RuntimeException("Fail to resolve the data source from path " + epPath2)
    }
  }

  private def persistStat(spark: SparkSession, savedResultOnPath: String, statePath: String) = {
    import spark.implicits._
    val statRdd = spark.sparkContext.makeRDD(Seq("SUCCESS", savedResultOnPath, new Date().toString), 4)
    statRdd.toDF.write.mode(SaveMode.Overwrite).text(statePath)
  }

  private def callERFlowLauncher(sparkConf: SparkAppConfiguration, sp: JoinSourcePair, flowSetting: FlowSetting) = {
    var flowArgs: Array[String] = JoinSourcePairSupport.conf2Args(sp)
    flowArgs ++= FlowSettingSupport.conf2Args(flowSetting)
    flowArgs ++= SparkAppConfigurationSupport.sparkConf2Args(sparkConf)
    log.info("flowArgs=" + flowArgs.toList)
    ERFlowLauncher.main(flowArgs)
  }
}
