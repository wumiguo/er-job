package org.wumiguo.erjob.io.exprlang

/**
 * @author levinliu
 *         Created on 2021/1/20
 *         (Change file header on Settings -> Editor -> File and Code Templates)
 */
object ExprLangEvaluator {
  /**
   * To replace the following exp-lang:
   * $NOW-1d or $NOW-1w or $NOW-1m
   * etc
   *
   * @param raw
   * @return
   */
  def eval(raw: Any): Any = {
    if (raw.isInstanceOf[Array[String]]) {
      val strs = raw.asInstanceOf[Array[String]]
      strs.map(Dictionary.lookup(_))
    } else if (raw.isInstanceOf[Seq[String]]) {
      val strs = raw.asInstanceOf[Seq[String]]
      strs.map(Dictionary.lookup(_))
    } else if (raw.isInstanceOf[List[String]]) {
      val strs = raw.asInstanceOf[List[String]]
      strs.map(Dictionary.lookup(_))
    } else if (raw.isInstanceOf[String]) {
      Dictionary.lookup(raw.asInstanceOf[String])
    } else {
      raw
    }
  }
}
