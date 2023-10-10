package com.amicusearch.etl.process.courtlistener.clusters

import com.amicusearch.etl.GenericAmicusearchTest
import com.amicusearch.etl.datatypes.courtlistener.ClusterWithNulls
import org.apache.spark.sql.Dataset
import org.scalatest.flatspec.AnyFlatSpec

class ClustersProcessingTest extends AnyFlatSpec with GenericAmicusearchTest {

  val processedNulls: Unit => Dataset[ClusterWithNulls] = courtListenerClusters andThen ClusterParseNulls()


  "ClustersProcessingTest" should "parse nulls" in {
    val df = processedNulls().toDF().coalesce(1)
    assertSnapshot("ProcessNulls", df, "id")
  }

}
