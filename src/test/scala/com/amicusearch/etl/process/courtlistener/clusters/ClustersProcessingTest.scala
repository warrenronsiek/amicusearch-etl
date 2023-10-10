package com.amicusearch.etl.process.courtlistener.clusters

import com.amicusearch.etl.GenericAmicusearchTest
import com.amicusearch.etl.datatypes.courtlistener.clusters.ClusterWithNulls
import org.apache.spark.sql.Dataset
import org.scalatest.flatspec.AnyFlatSpec

class ClustersProcessingTest extends AnyFlatSpec with GenericAmicusearchTest {


  "ClustersProcessingTest" should "parse nulls" in {
    val df = opinionProcessedNulls().toDF().coalesce(1)
    assertSnapshot("ProcessNulls", df, "id")
  }

}
