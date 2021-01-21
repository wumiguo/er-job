package org.wumiguo.erjob.argsparser

import org.wumiguo.erjob.io.configuration.{Input, Output, SourcePair}

/**
 * @author levinliu
 *         Created on 2021/1/21
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
object SourcePairSupport {

  def conf2Args(input: Input, output: Output, sp: SourcePair): Array[String] = {
    val epPath1 = input.path + "/" + sp.sourcePair(0)
    val epPath2 = input.path + "/" + sp.sourcePair(1)
    var flowArgs = Array[String]()
    flowArgs :+= "dataSet1=" + epPath1
    val id1 = if (sp.idFields.size >= 1) {
      sp.idFields(0)
    } else {
      ""
    }
    flowArgs :+= "dataSet1-id=" + id1
    flowArgs :+= "dataSet1-attrSet=" + sp.joinFields.map(_.source1Field).reduce(_ + "," + _)
    val addFields1 = additionAttrSet2Str(sp.source1AdditionalExtractFields)
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
    val addFields2 = additionAttrSet2Str(sp.source2AdditionalExtractFields)
    flowArgs :+= "dataSet2-additionalAttrSet=" + addFields2
    flowArgs :+= "dataSet2-filterSize=" + sp.source2Filters.size
    sp.source2Filters.zipWithIndex.foreach(x => flowArgs :+= "dataSet2-filter" + x._2 + "=" + x._1.field + ":" + x._1.values.mkString(","))
    flowArgs :+= "joinFieldsWeight=" + sp.joinFields.map(_.weight.toString).reduce(_ + "," + _)
    flowArgs :+= "outputPath=" + output.getPath
    flowArgs :+= "outputType=" + output.getDataType
    flowArgs :+= "joinResultFile=" + sp.joinResultFile
    flowArgs :+= "overwriteOnExist=" + output.getOverwriteOnExist
    flowArgs :+= "showSimilarity=" + output.getShowSimilarity
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
