package com.amicusearch.etl.utils

import com.amicusearch.etl.{AppParams, GenericAmicusearchTest}
import org.scalatest.flatspec.AnyFlatSpec
import com.typesafe.scalalogging.LazyLogging
import upickle.default.macroRW
import util.retry.blocking.{Failure, Retry, RetryStrategy, Success}
import upickle.default.{macroRW, ReadWriter => RW}
import upickle.default._
import util.retry.blocking.RetryStrategy.RetryStrategyProducer
import scala.concurrent.duration._

class MLServerGetCitationsTest  extends AnyFlatSpec with GenericAmicusearchTest {

  val mlgc = new MLServerGetCitations(AppParams.Environment.local, "http://localhost:5000/get_citations")

  import mlgc._

  "Citation" should "be serializable" in {
    val citation = Citation(full = "42 U.S.C. \\u00a7 1983", cite_type = "FullLawCitation")
    assert(citation == read[Citation](write(citation)))
  }

  it should "parse dummy cite" in {
    val c = read[Citation]("""{"cite_type": "FullLawCitation", "full": "42 U.S.C. \\u00a7 1983", "reporter": "U.S.C.", "section": "1983", "title": "42"}""")
    assert(c == Citation(cite_type = "FullLawCitation", full = "42 U.S.C. \\u00a7 1983"))
  }


  "Citation getter" should "get citations" in {
    val citations = mlgc.getCitations("This is a short opinion.")
    assert(citations == List(Citation(full = "42 U.S.C. \\u00a7 1983", cite_type = "FullLawCitation").full))
  }

}
