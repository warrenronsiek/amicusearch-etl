package com.amicusearch.etl.read

import com.amicusearch.etl.GenericAmicusearchTest
import org.scalatest.flatspec.AnyFlatSpec
import com.amicusearch.etl.read.ReadCourtsDB
import org.apache.spark.sql.{Dataset, Row}

class ReadCourtsDBTest extends AnyFlatSpec with GenericAmicusearchTest {
  "ReadCourts" should "read courts data" in {
    assertSnapshot("ReadCourts", courtsDB.apply().toDF().coalesce(1), "id")
  }
}
