package com.amicusearch.etl.partition.casetext

import com.amicusearch.etl.datatypes.CasetextCase
import com.amicusearch.etl.{AppParams, GenericAmicusearchTest}
import com.amicusearch.etl.read.ReadRawCasetext
import org.apache.spark.sql.Dataset
import org.scalatest.flatspec.AnyFlatSpec

class ParsingTest extends AnyFlatSpec with GenericAmicusearchTest {

  "parsing functions" should "parse titles" in {
    val title = ParseTitle.parseTitle("<p><span class=\"font-weight-bold mr-1\">Full title:</span>Nathan Samuel: Appellant: v. 2345 Ocean Ave. Associates: LLC: Respondent.</p>")
    assert(title.contains("Nathan Samuel: Appellant: v. 2345 Ocean Ave. Associates: LLC: Respondent."))
  }

  it should "parse courts" in {
    val court = ParseCourt.parseCourt(Some("<p><span class=\"font-weight-bold mr-1\">Court:</span>Supreme Court, Kings County, New York.</p>"))
    assert(court.contains("Supreme Court, Kings County, New York."))
  }

  it should "parse dates" in {
    val date = ParseDate.parseDate(Some("<p><span class=\"font-weight-bold mr-1\">Date published: </span><time pubdate itemprop=\"datePublished\" datetime=\"2017-06-12\"></time>Jun 12, 2017</p>"))
    assert(date.contains("2017-06-12"))
  }

  it should "parse citations" in {
    val citation = ParseCitation.parseCitation(Some("<div class=\"citation d-inline-block mb-2\">58 Misc. 3d 592 (N.Y. Crim. Ct. 2017)</div>"))
    assert(citation.contains("58 Misc. 3d 592 (N.Y. Crim. Ct. 2017)"))
  }

  it should "parse documents" in {
    val document = ParseDocument.parseDocument(Some(scala.io.Source.fromFile("src/test/resources/example_casetext_case.html").mkString))
    assert(document.contains("PRESENT: Schoenfeld: J.P.: Lowe: III: Ling-Cohan: JJ¶Decision Date: February 22: 2017"))
  }

  val parsedTitle: Unit => Dataset[CasetextCase] =
    ReadRawCasetext("src/test/resources/casetextsample.jsonl", AppParams.Environment.local) andThen ParseTitle()

  val parsedCourt: Unit => Dataset[CasetextCase] = parsedTitle andThen ParseCourt()

  val parsedDate: Unit => Dataset[CasetextCase] = parsedCourt andThen ParseDate()

  val parseCitation: Unit => Dataset[CasetextCase] = parsedDate andThen ParseCitation()

  val parseDocument: Unit => Dataset[CasetextCase] = parseCitation andThen ParseDocument()

  "ParseTitle" should "parse title" in {
    val ds = parsedTitle().toDF().coalesce(1)
    assertSnapshot("ParseTitle", ds, "url")
  }

  "ParseCourt" should "parse court" in {
    val ds = parsedCourt().toDF().coalesce(1)
    assertSnapshot("ParseCourt", ds, "url")
  }

  "ParseDate" should "parse date" in {
    val ds = parsedDate().toDF().coalesce(1)
    assertSnapshot("ParseDate", ds, "url")
  }

  "ParseCitation" should "parse citation" in {
    val ds = parseCitation().toDF().coalesce(1)
    assertSnapshot("ParseCitation", ds, "url")
  }

  "ParseDocument" should "parse document" in {
    val ds = parseDocument().toDF().coalesce(1)
    assertSnapshot("ParseDocument", ds, "url")
  }
}
