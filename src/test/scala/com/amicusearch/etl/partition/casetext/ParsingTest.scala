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

  it should "parse courts2" in {
    val court = ParseCourt.parseCourt(Some("<p><span class=\"font-weight-bold mr-1\">Court:</span>Appellate Division of the Supreme Court of New York: First Department</p>"))
    assert(court.contains("Appellate Division of the Supreme Court of New York: First Department"))
  }

  it should "parse courts3" in {
    val court = ParseCourt.parseCourt(Some("<p><span class=\"font-weight-bold mr-1\">Court:</span>Supreme Court: Appellate Term: Second Dept.: 9th &amp; 10th Judicial Districts</p>"))
    assert(court.contains("Supreme Court: Appellate Term: Second Dept.: 9th & 10th Judicial Districts"))
  }

  val parsedTitle: Unit => Dataset[CasetextCase] =
    ReadRawCasetext("src/test/resources/casetextsample.jsonl", AppParams.Environment.local) andThen ParseTitle()

  val parsedCourt: Unit => Dataset[CasetextCase] = parsedTitle andThen ParseCourt()

  "ParseTitle" should "parse title" in {
    val ds = parsedTitle().toDF().coalesce(1)
    assertSnapshot("ParseTitle", ds, "url")
  }

  "ParseCourt" should "parse court" in {
    val ds = parsedCourt().toDF().coalesce(1)
    assertSnapshot("ParseCourt", ds, "url")
  }
}
