package com.amicusearch.etl.process.courtlistener.joins

import com.amicusearch.etl.GenericAmicusearchTest
import org.scalatest.flatspec.AnyFlatSpec

class JoinsTest extends AnyFlatSpec with GenericAmicusearchTest{

  "CourtToDocketJoin" should "join rows" in {
    val df = courtsJoinedDockets.toDF().coalesce(1)
    assertSnapshot("CourtsToDockets", df, "docket_id")
  }

  "DocketToClusterJoin" should "join rows" in {
    val df = docketsJoinedClusters.toDF().coalesce(1)
    assertSnapshot("DocketsToClusters", df, "docket_id")
  }

  "ClusterToOpinionJoin" should "join rows" in {
    val df = opinionsJoinedClusters.toDF().coalesce(1)
    assertSnapshot("ClustersToOpinions", df, "opinion_id")
  }

  "OpinionToCitationJoin" should "join rows" in {
    val df = opinionsToCitations(opinionsJoinedClusters).toDF().coalesce(1)
    assertSnapshot("OpinionsToCitations", df, "opinion_id")
  }
}
