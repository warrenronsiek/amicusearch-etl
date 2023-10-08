package com.amicusearch.etl.process.courtlistener.opinions

import com.amicusearch.etl.GenericAmicusearchTest
import com.amicusearch.etl.datatypes.courtlistener.OpinionsWithNulls
import org.apache.spark.sql.Dataset
import org.scalatest.flatspec.AnyFlatSpec

import scala.xml.Elem

class OpinionsProcessingTest extends AnyFlatSpec with GenericAmicusearchTest {

  val processedNulls: Unit => Dataset[OpinionsWithNulls] = courtListenerOpinions andThen ParseNulls()


  "OpinionsProcessing" should "parse nulls" in {
    assertSnapshot("ParseNulls", processedNulls().toDF().coalesce(1), "id")
  }
}
