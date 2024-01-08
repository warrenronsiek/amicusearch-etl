package com.amicusearch.etl.utils

import com.amicusearch.etl.AppParams
import org.scalatest.flatspec.AnyFlatSpec

import upickle.default.{macroRW, ReadWriter => RW}
import upickle.default._

class MLServerGetCitationsTest extends AnyFlatSpec {

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

  it should "parce citation array" in {
    val c = read[Response]("""{"citations":[{"volume":"413","reporter":"So. 2d","page":"877","cite_type":"FullCaseCitation","full":"413 So. 2d 877 (1982","year":"1982"}]}""")
    assert(c == Response(List(Citation(cite_type = "FullCaseCitation", full = "413 So. 2d 877 (1982"))))
  }

  "Citation getter" should "get citations" in {
    val citations = mlgc.getCitations("This is a short opinion.")
    assert(citations == List(Citation(full = "42 U.S.C. \\u00a7 1983", cite_type = "FullLawCitation").full))
  }

}
