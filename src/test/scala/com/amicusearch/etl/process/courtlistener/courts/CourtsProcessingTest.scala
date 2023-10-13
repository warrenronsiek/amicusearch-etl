package com.amicusearch.etl.process.courtlistener.courts

import com.amicusearch.etl.GenericAmicusearchTest
import com.amicusearch.etl.datatypes.courtlistener.courts.Court
import org.apache.spark.sql.Dataset
import org.scalatest.flatspec.AnyFlatSpec

class CourtsProcessingTest extends AnyFlatSpec with GenericAmicusearchTest {

  val parsedCourts: Unit => Dataset[Court] = courtListenerCourts andThen ParseCourts()
  val filteredCourts: Unit => Dataset[Court] = parsedCourts andThen FilterCourts()

  "CourtsFilter" should "identify courts with state names in their names" in {
    assert(FilterCourts.nameContainsRegion(List("Supreme Court of North Carolina"), List("North Carolina")))
  }

  it should "remove courts without state names in their names" in {
    assert(!FilterCourts.nameContainsRegion(List("Supreme Court of North Carolina"), List("South Carolina")))
  }

  it should "handle variable length lists" in {
    assert(FilterCourts.nameContainsRegion(List("Supreme Court of North Carolina", "Sup.Ct. of SC"), List("North Carolina", "New York")))
  }

  "CourtsProcessingTest" should "parse nulls" in {
    val df = parsedCourts().toDF().coalesce(1)
    assertSnapshot("ParsedCourts", df, "id")
  }

  it should "return all filter courts" in {
    val df = filteredCourts().toDF().coalesce(1)
    assertSnapshot("NoFilterCourts", df, "id")
  }

  it should "return only filter courts" in {
    val df = (parsedCourts andThen FilterCourts(List("North Carolina"), includeFederal = true))().toDF().coalesce(1)
    assertSnapshot("FilteredCourts", df, "id")
  }
}
