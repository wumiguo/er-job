package org.wumiguo.erjob

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

import java.io.{File, FileInputStream}

import org.scalatest.flatspec.AnyFlatSpec
import org.yaml.snakeyaml.error.YAMLException


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

  it should "fail to load a bad yaml " in {
    val yamlPath = TestDirs.resolveTestResourcePath("sample/yml/badsample.yml")
    val input = new FileInputStream(new File(yamlPath))
    val yaml = new Yaml(new Constructor(classOf[SampleYml]))
    var hasErr = false
    try {
      val e = yaml.load(input).asInstanceOf[SampleYml]
      println("data=" + e)
      assert(false, "this line shouldn't be run as it must have issue on loading a bad yaml")
    }
    catch {
      case e =>
        hasErr = true
        assert(e.isInstanceOf[YAMLException])
        println("error=" + e.getMessage)
    }
    assert(hasErr, "it must have error")
  }
}

