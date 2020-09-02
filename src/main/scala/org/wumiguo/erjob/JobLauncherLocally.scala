package org.wumiguo.erjob

import java.io.File

import org.apache.spark.sql.{SaveMode, SparkSession}
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
object JobLauncherLocally extends SparkEnvSetup {

  def main(args: Array[String]): Unit = {
    log.info("start to run er job locally")
    val outputDir = new File("/tmp/data-er")
    if (!outputDir.exists()) {
      outputDir.mkdirs()
    }
    val jobConfPath = CommandLineUtil.getParameter(args, "jobConfPath", "src/main/resources/er-job-configuration.yml")
    val erJobConf = ERJobConfigurationLoader.load(jobConfPath)
    val output = erJobConf.getOutput
    val spark = createLocalSparkSession(getClass.getName, outputDir = output.path)
    JobLauncher.main(args)
  }
}
