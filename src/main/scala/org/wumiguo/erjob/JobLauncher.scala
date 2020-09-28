package org.wumiguo.erjob

import org.wumiguo.erjob.io.ApplicationConfigurationLoader
import org.wumiguo.ser.common.{SparkAppConfigurationSupport, SparkEnvSetup}
import org.wumiguo.ser.methods.util.CommandLineUtil


/**
 * @author levinliu
 *         Created on 2020/7/15
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
object JobLauncher extends SparkEnvSetup {

  def main(args: Array[String]): Unit = {
    val appConfPath = CommandLineUtil.getParameter(args, "appConfPath", "src/main/resources/application.yml")
    val sparkConf = ApplicationConfigurationLoader.loadSparkConf(appConfPath)
    log.info("sparkConf=" + sparkConf)
    val appSettingArgs = SparkAppConfigurationSupport.sparkConf2Args(sparkConf)
    val argsWithAppSetting = args ++ appSettingArgs
    BaseJobLauncher.main(argsWithAppSetting)
  }
}
