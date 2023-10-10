package com.amicusearch.etl.process.courtlistener.dockets

import com.amicusearch.etl.GenericAmicusearchTest
import com.amicusearch.etl.datatypes.courtlistener.dockets.DocketsWithNulls
import org.apache.spark.sql.Dataset
import org.scalatest.flatspec.AnyFlatSpec

class DocketsProcessingTest extends AnyFlatSpec with GenericAmicusearchTest {

  val parseDockets: Unit => Dataset[DocketsWithNulls] = courtListenerDockets andThen ParseDockets()

  "DocketsProcessingTest" should "parse nulls" in {
    val df = parseDockets().toDF().coalesce(1)
    assertSnapshot("ParsedDockets", df, "id")
  }
}
