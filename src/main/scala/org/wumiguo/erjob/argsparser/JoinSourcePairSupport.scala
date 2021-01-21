package org.wumiguo.erjob.argsparser

import org.wumiguo.erjob.io.configuration.JoinSourcePair

/**
 * @author levinliu
 *         Created on 2021/1/21
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
object JoinSourcePairSupport {

  def conf2Args(sp: JoinSourcePair) = {
    var flowArgs = Array[String]()
    flowArgs :+= "dataSet1=" + sp.firstSource.loadDataFromPath
    flowArgs :+= "dataSet1-id=" + sp.firstSource.idField
    flowArgs :+= "dataSet1-attrSet=" + sp.joinRules.map(_.firstSourceField).reduce(_ + "," + _)
    val addFields1 = additionAttrSet2Str(sp.joinResult.showFieldFromFirstSource)
    flowArgs :+= "dataSet1-additionalAttrSet=" + addFields1
    flowArgs :+= "dataSet1-filterSize=" + sp.firstSource.filterOnFields.size
    sp.firstSource.filterOnFields.zipWithIndex.foreach(x => flowArgs :+= "dataSet1-filter" + x._2 + "=" + x._1.field + ":" + x._1.values.mkString(","))
    flowArgs :+= "dataSet2=" + sp.secondSource.loadDataFromPath
    flowArgs :+= "dataSet2-id=" + sp.secondSource.idField
    flowArgs :+= "dataSet2-attrSet=" + sp.joinRules.map(_.secondSourceField).reduce(_ + "," + _)
    val addFields2 = additionAttrSet2Str(sp.joinResult.showFieldFromSecondSource)
    flowArgs :+= "dataSet2-additionalAttrSet=" + addFields2
    flowArgs :+= "dataSet2-filterSize=" + sp.secondSource.filterOnFields.size
    sp.secondSource.filterOnFields.zipWithIndex.foreach(x => flowArgs :+= "dataSet2-filter" + x._2 + "=" + x._1.field + ":" + x._1.values.mkString(","))
    flowArgs :+= "joinFieldsWeight=" + sp.joinRules.map(_.weight.toString).reduce(_ + "," + _)
    val output = sp.joinResult
    val savedResultOnPath = output.savedResultOnPath
    flowArgs :+= "outputPath=" + savedResultOnPath.substring(0, savedResultOnPath.lastIndexOf("/"))
    flowArgs :+= "outputType=" + savedResultOnPath.substring(savedResultOnPath.lastIndexOf(".") + 1)
    flowArgs :+= "joinResultFile=" + savedResultOnPath.substring(savedResultOnPath.lastIndexOf("/") + 1, savedResultOnPath.lastIndexOf("."))
    flowArgs :+= "overwriteOnExist=" + output.overwriteOnExist
    flowArgs :+= "showSimilarity=" + output.showSimilarity
    flowArgs :+= "connectedClustering=" + output.connectedClustering
    flowArgs
  }

  private def additionAttrSet2Str(additionalAttrSet: Array[String]) = {
    if (additionalAttrSet == null || additionalAttrSet.isEmpty) {
      ""
    } else {
      additionalAttrSet.reduce(_ + "," + _)
    }
  }

}
