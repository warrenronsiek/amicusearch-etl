package com.amicusearch.etl.process.courtlistener.courts

import com.amicusearch.etl.GenericAmicusearchTest
import com.amicusearch.etl.datatypes.courtlistener.courts.Court
import com.amicusearch.etl.utils.USRegion
import org.apache.spark.sql.Dataset
import org.scalatest.flatspec.AnyFlatSpec
import org.slf4j.Logger
import org.slf4j.LoggerFactory
class CourtsProcessingTest extends AnyFlatSpec with GenericAmicusearchTest {

  val parsedCourts: Unit => Dataset[Court] = courtListenerCourts andThen ParseCourts()
  val filteredCourts: Unit => Dataset[Court] = parsedCourts andThen FilterCourts()
  val filteredCourtsByID: Unit => Dataset[Court] = parsedCourts andThen FilterCourts(List("caljustctbut", "gaoyertermct"))
  logger.info("foo!")

  "CourtsFilter" should "identify courts with state names in their names" in {
    assert(FilterCourts.nameContainsRegion(List("Supreme Court of North Carolina"), List(USRegion.north_carolina)))
  }

  it should "remove courts without state names in their names" in {
    assert(!FilterCourts.nameContainsRegion(List("Supreme Court of North Carolina"),  List(USRegion.south_carolina)))
  }

  it should "handle variable length lists" in {
    assert(FilterCourts.nameContainsRegion(List("Supreme Court of North Carolina", "Sup.Ct. of SC"), List(USRegion.north_carolina, USRegion.new_york)))
  }

  it should "filter by court ids" in {
    assertSnapshot("FilteredCourtsByID", filteredCourtsByID().toDF().coalesce(1), "id")
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
    val df = (parsedCourts andThen FilterCourts(List(), List(USRegion.north_carolina), includeFederal = true))().toDF().coalesce(1)
    assertSnapshot("FilteredCourts", df, "id")
  }
}
