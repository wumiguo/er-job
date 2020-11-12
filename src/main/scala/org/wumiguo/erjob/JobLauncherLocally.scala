package org.wumiguo.erjob

import java.io.File

import org.wumiguo.erjob.io.{ApplicationConfigurationLoader, ERJobConfigurationLoader}
import org.wumiguo.ser.common.{SparkAppConfigurationSupport, SparkEnvSetup}
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
    val appConfPath = "src/main/resources/application-local.yml"
    val sparkConf = ApplicationConfigurationLoader.loadSparkConf(appConfPath)
    val jobConfPath = "src/main/resources/er-job-configuration.yml"
    val confArgs = Seq(
      "appConfPath=" + appConfPath,
      "jobConfPath=" + jobConfPath
    )
    val mergeArgs = args ++ SparkAppConfigurationSupport.sparkConf2Args(sparkConf) ++ confArgs
    BaseJobLauncher.main(mergeArgs)
  }
}
