package org.wumiguo.erjob

import java.io.{File}

import org.apache.spark.sql.SaveMode
import org.wumiguo.erjob.io.{ERJobConfigurationLoader, Input, Output, SourcePair}
import org.wumiguo.ser.ERFlowLauncher
import org.wumiguo.ser.common.SparkEnvSetup


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
    val sourcePairs = erJobConf.getSourcesPairs
    var sourceCounter = 0
    val outputDir = new File("/tmp/data-er")
    if (!outputDir.exists()) {
      outputDir.mkdirs()
    }
    val spark = createLocalSparkSession(getClass.getName, outputDir = output.path)
    var statPathArr = Array[String]()
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
            callERFlowLauncher(input, output, sp, epPath1, epPath2)
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
        callERFlowLauncher(input, output, sp, epPath1, epPath2)
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
    val mapping1 = sparkSession.read.text(statPathArr(0)).rdd
    mapping1.foreach(x => log.info("mapping1=" + x))
    val mapping1Path = mapping1.filter(_ != "SUCCESS").collect()(0).getAs[String](0)
    val mapping2 = sparkSession.read.text(statPathArr(1)).rdd
    mapping2.foreach(x => log.info("mapping2=" + x))
    val mapping2Path = mapping2.filter(_ != "SUCCESS").collect()(0).getAs[String](0)
    log.info("mapping path=" + mapping1Path)
    val d = sparkSession.read.csv(mapping1Path).rdd
    d.foreach(x => println("mapping1 entry=" + x))
    val d2 = sparkSession.read.csv(mapping1Path).rdd
    d2.foreach(x => println("mapping2 entry=" + x))
    log.info("er job is completed" + d)
  }

  private def callERFlowLauncher(input: Input, output: Output, sp: SourcePair, epPath1: String, epPath2: String) = {
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
    flowArgs +:= "outputPath=" + output.getPath
    flowArgs +:= "outputType=" + output.getDataType
    flowArgs +:= "joinResultFile=" + sp.joinResultFile
    flowArgs +:= "overwriteOnExist=" + output.getOverwriteOnExist
    ERFlowLauncher.main(flowArgs)
  }
}
