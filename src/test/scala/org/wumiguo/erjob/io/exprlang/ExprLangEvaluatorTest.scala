package org.wumiguo.erjob.io.exprlang

import java.text.SimpleDateFormat
import java.util.Date

import org.scalatest.flatspec.AnyFlatSpec

/**
 * @author levinliu
 *         Created on 2021/1/21
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
class ExprLangEvaluatorTest extends AnyFlatSpec {
  it should "eval " in {
    val value = Seq("$today", "20210101")
    val actual = ExprLangEvaluator.eval(value)
    val today = new SimpleDateFormat("YYYYMMDD").format(new Date())
    assertResult(Seq(today, "20210101"))(actual)
  }
}
