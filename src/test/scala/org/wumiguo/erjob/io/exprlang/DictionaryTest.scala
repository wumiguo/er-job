package org.wumiguo.erjob.io.exprlang

import java.text.SimpleDateFormat
import java.util.Date

import org.scalatest.flatspec.AnyFlatSpec

/**
 * @author levinliu
 *         Created on 2021/1/21
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
class DictionaryTest extends AnyFlatSpec {
  it should "lookup " in {
    val value = "$today"
    val actual = Dictionary.lookup(value)
    assertResult(new SimpleDateFormat("YYYYMMDD").format(new Date()))(actual)
  }
}
