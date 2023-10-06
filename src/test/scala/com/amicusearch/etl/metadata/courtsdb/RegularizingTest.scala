package com.amicusearch.etl.metadata.courtsdb

import com.amicusearch.etl.GenericAmicusearchTest
import com.amicusearch.etl.datatypes.CourtsDBCourt
import org.apache.spark.sql.Dataset
import org.scalatest.flatspec.AnyFlatSpec

class RegularizingTest extends AnyFlatSpec with GenericAmicusearchTest{

  val regularizeJurisdiction: Unit => Dataset[CourtsDBCourt] = courtsDB andThen RegularizeJurisdiction()

  "RegularizeJurisdiction" should "remove periods from the jurisdiction column" in {
    assertSnapshot("RegularizeJurisdiction", regularizeJurisdiction.apply().toDF().coalesce(1), "id")
  }
}
