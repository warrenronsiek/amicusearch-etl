package com.amicusearch.etl.utils

import com.amicusearch.etl.AppParams
import com.amicusearch.etl.utils.serde.Citation
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization.write
import org.scalatest.flatspec.AnyFlatSpec

class MLServerGetCitationsTest  extends AnyFlatSpec {

  val mlgc = new MLServerGetCitations(AppParams.Environment.local, "http://localhost:5000/get_citations")

  import mlgc._

  "Citation" should "be serializable" in {
    val citation = Citation(full = "42 U.S.C. \\u00a7 1983", cite_type = "FullLawCitation")
    assert(citation == parse(write(citation)).extract[Citation])
  }

  it should "parse dummy cite" in {
    val c = parse("""{"cite_type": "FullLawCitation", "full": "42 U.S.C. \\u00a7 1983", "reporter": "U.S.C.", "section": "1983", "title": "42"}""").extract[Citation]
    assert(c == Citation(cite_type = "FullLawCitation", full = "42 U.S.C. \\u00a7 1983"))
  }


  "Citation getter" should "get citations" in {
    val citations = mlgc.getCitations("This is a short opinion.")
    assert(citations == List(Citation(full = "42 U.S.C. \\u00a7 1983", cite_type = "FullLawCitation").full))
  }

}
