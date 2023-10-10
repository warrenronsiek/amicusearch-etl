package com.amicusearch.etl.process.courtlistener.joins

import com.amicusearch.etl.GenericAmicusearchTest
import com.amicusearch.etl.datatypes.courtlistener.{CourtDocket, DocketsWithNulls}
import com.amicusearch.etl.datatypes.courtlistener.courts.Court
import com.amicusearch.etl.process.courtlistener.courts.{FilterCourts, ParseCourts}
import com.amicusearch.etl.process.courtlistener.dockets.ParseDockets
import org.apache.spark.sql.Dataset
import org.scalatest.flatspec.AnyFlatSpec

class JoinsTest extends AnyFlatSpec with GenericAmicusearchTest{

  val courts: Unit => Dataset[Court] = courtListenerCourts andThen ParseCourts() andThen FilterCourts()
  val parseDockets: Unit => Dataset[DocketsWithNulls] = courtListenerDockets andThen ParseDockets()
  val courtsToDockets: Dataset[DocketsWithNulls] => Dataset[CourtDocket] = CourtsToDockets(courts())

  "CourtToDocketJoin" should "join rows" in {
    val df = courtsToDockets(parseDockets()).toDF().coalesce(1)
    assertSnapshot("CourtsToDockets", df, "docket_id")
  }

}
