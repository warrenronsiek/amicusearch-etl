package com.amicusearch.etl.process.courtlistener.citations

import com.amicusearch.etl.GenericAmicusearchTest
import com.amicusearch.etl.datatypes.courtlistener.citations.ParsedCitation
import org.apache.spark.sql.Dataset
import org.scalatest.flatspec.AnyFlatSpec

class CitationsProcessingSuite extends AnyFlatSpec with GenericAmicusearchTest {


  "CitationsProcessingSuite" should "parse citations" in {
    val df = processedCitations().toDF().coalesce(1)
    assertSnapshot("ParseCitations", df, "id")
  }
}
