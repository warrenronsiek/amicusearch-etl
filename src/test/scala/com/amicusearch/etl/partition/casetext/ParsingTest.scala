package com.amicusearch.etl.partition.casetext

import com.amicusearch.etl.datatypes.CasetextCase
import com.amicusearch.etl.{AppParams, GenericAmicusearchTest}
import com.amicusearch.etl.read.ReadRawCasetext
import org.apache.spark.sql.Dataset
import org.scalatest.flatspec.AnyFlatSpec

class ParsingTest extends AnyFlatSpec with GenericAmicusearchTest {

  val parsedTitle: Unit => Dataset[CasetextCase] =
    ReadRawCasetext("src/test/resources/casetextsample.jsonl", AppParams.Environment.local) andThen ParseTitle()


  "parsing function" should "parse html" in {
    val html = "<p><span class=\"font-weight-bold mr-1\">Full title:</span>Nathan Samuel: Appellant: v. 2345 Ocean Ave. Associates: LLC: Respondent.</p>"
    val title = ParseTitle.parseTitle(html)
    assert(title == "Nathan Samuel: Appellant: v. 2345 Ocean Ave. Associates: LLC: Respondent.")
  }

  "ParseTitle" should "parse title" in {
    val ds = parsedTitle().toDF().coalesce(1)
    assertSnapshot("ParseTitle", ds, "url")
  }
}
