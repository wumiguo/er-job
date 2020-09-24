package org.wumiguo.erjob

import java.io.File

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.wumiguo.erjob.JobLauncherLocally.{createLocalSparkSession, getClass, log}
import org.wumiguo.erjob.io.configuration.flow.FlowSetting
import org.wumiguo.erjob.io.configuration.{Input, Output, SourcePair}
import org.wumiguo.erjob.io.{ApplicationConfigurationLoader, ERJobConfigurationLoader, FlowsConfigurationLoader}
import org.wumiguo.ser.ERFlowLauncher
import org.wumiguo.ser.common.{SparkAppConfigurationSupport, SparkEnvSetup}
import org.wumiguo.ser.methods.util.CommandLineUtil


/**
 * @author levinliu
 *         Created on 2020/7/15
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
object JobLauncher extends SparkEnvSetup {

  def main(args: Array[String]): Unit = {
    val appConfPath = CommandLineUtil.getParameter(args, "appConfig", "src/main/resources/application.yml")
    val sparkConf = ApplicationConfigurationLoader.loadSparkConf(appConfPath)
    log.info("sparkConf=" + sparkConf)
    val appSettingArgs = SparkAppConfigurationSupport.sparkConf2Args(sparkConf)
    val argsWithAppSetting = args ++ appSettingArgs
    BaseJobLauncher.main(argsWithAppSetting)
  }
}
