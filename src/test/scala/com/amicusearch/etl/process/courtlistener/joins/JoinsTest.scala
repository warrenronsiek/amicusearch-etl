package com.amicusearch.etl.process.courtlistener.joins

import com.amicusearch.etl.GenericAmicusearchTest
import com.amicusearch.etl.datatypes.courtlistener.clusters.ClusterWithNulls
import com.amicusearch.etl.datatypes.courtlistener.courts.Court
import com.amicusearch.etl.datatypes.courtlistener.dockets.DocketsWithNulls
import com.amicusearch.etl.datatypes.courtlistener.joins.{CourtDocket, DocketCluster}
import com.amicusearch.etl.process.courtlistener.clusters.ClusterParseNulls
import com.amicusearch.etl.process.courtlistener.courts.{FilterCourts, ParseCourts}
import com.amicusearch.etl.process.courtlistener.dockets.ParseDockets
import org.apache.spark.sql.Dataset
import org.scalatest.flatspec.AnyFlatSpec

class JoinsTest extends AnyFlatSpec with GenericAmicusearchTest{

  val courts: Unit => Dataset[Court] = courtListenerCourts andThen ParseCourts() andThen FilterCourts()
  val clusters: Unit => Dataset[ClusterWithNulls] = courtListenerClusters andThen ClusterParseNulls()
  val parseDockets: Unit => Dataset[DocketsWithNulls] = courtListenerDockets andThen ParseDockets()
  val courtsToDockets: Dataset[DocketsWithNulls] => Dataset[CourtDocket] = CourtsToDockets(courts())
  val courtsJoinedDockets: Dataset[CourtDocket] = courtsToDockets(parseDockets())
  val docketsToClusters: Dataset[ClusterWithNulls] => Dataset[DocketCluster] = DocketsToClusters(courtsJoinedDockets)
  val docketsJoinedClusters: Dataset[DocketCluster] = docketsToClusters(clusters())

  "CourtToDocketJoin" should "join rows" in {
    val df = courtsJoinedDockets.toDF().coalesce(1)
    assertSnapshot("CourtsToDockets", df, "docket_id")
  }

  "DocketToClusterJoin" should "join rows" in {
    val df = docketsJoinedClusters.toDF().coalesce(1)
    assertSnapshot("DocketsToClusters", df, "docket_id")
  }

}
