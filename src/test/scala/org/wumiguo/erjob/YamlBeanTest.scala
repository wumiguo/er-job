package org.wumiguo.erjob

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

import scala.collection.mutable.ListBuffer
import java.io.{File, FileInputStream}
import java.util

import org.scalatest.flatspec.AnyFlatSpec

import scala.beans.BeanProperty

class YamlBeanTest extends AnyFlatSpec {
  it should "load a valid yaml " in {
    val yamlPath = TestDirs.resolveTestResourcePath("sample/yml/sample.yml")
    val input = new FileInputStream(new File(yamlPath))
    val yaml = new Yaml(new Constructor(classOf[SampleYml]))
    val e = yaml.load(input).asInstanceOf[SampleYml]
    println("data=" + e)
    assert(e.date == "20200715")
    assert(e.title == "Sample Yaml Data")
    assert(e.owner == "lmh")
    assert(e.tags.toArray().toSeq == Seq("apple", "ali", "amazon"))
  }
}

