package com.amicusearch.etl

import org.scalatest.flatspec.AnyFlatSpec
import com.amicusearch.etl.RunCLOpinionProcessor
import com.amicusearch.etl.datatypes.courtlistener.citations.{CollectedCitation, ParsedCitation}
import com.amicusearch.etl.datatypes.courtlistener.clusters.ClusterWithNulls
import com.amicusearch.etl.datatypes.courtlistener.courts.Court
import com.amicusearch.etl.datatypes.courtlistener.dockets.DocketsWithNulls
import com.amicusearch.etl.datatypes.courtlistener.joins.OpinionCitation
import com.amicusearch.etl.datatypes.courtlistener.opinions.OpinionsCleanWhitespace
import com.amicusearch.etl.datatypes.courtlistener.transforms.{OpinionOutboundCitations, OpinionSummary}
import org.apache.spark.sql.Dataset

class RunCLOpinionProcessorTest extends AnyFlatSpec with GenericAmicusearchTest {

  lazy val cts: Dataset[Court] = RunCLOpinionProcessor.processCourts("src/test/resources/courtlistener_courts_sample.jsonl",
    List(), AppParams.Environment.local, List(), true).apply().cache()

  "Courts" should "match snapshot" in {
    assertSnapshot("ComposedCourts", cts.toDF().coalesce(1), "id")
  }

  lazy val dkts: Dataset[DocketsWithNulls] = RunCLOpinionProcessor.processDockets("src/test/resources/courtlistener_dockets_sample.jsonl",
    AppParams.Environment.local).apply().cache()

  "Dockets" should "match snapshot" in {
    assertSnapshot("ComposedDockets", dkts.toDF().coalesce(1), "id")
  }

  lazy val clstrs: Dataset[ClusterWithNulls] = RunCLOpinionProcessor.processClusters("src/test/resources/courtlistener_opinion_cluster_sample.jsonl",
    AppParams.Environment.local).apply().cache()

  "Clusters" should "match snapshot" in {
    assertSnapshot("ComposedClusters", clstrs.toDF().coalesce(1), "id")
  }

  lazy val opn: Dataset[OpinionsCleanWhitespace] = RunCLOpinionProcessor.processOpinions("src/test/resources/courtlistener_opinions_sample.jsonl",
    AppParams.Environment.local).apply().cache()

  "Opinions" should "match snapshot" in {
    assertSnapshot("ComposedOpinions", opn.toDF().coalesce(1), "id")
  }

  lazy val citations: Dataset[CollectedCitation] = RunCLOpinionProcessor.processCitations("src/test/resources/courtlistener_citations_sample.jsonl",
    AppParams.Environment.local).apply().cache()

  // snapshot tests don't test arrays - so we cant test this for now
//  "Citations" should "match snapshot" in {
//    assertSnapshot("ComposedCitations", citations.toDF().coalesce(1), "id")
//  }

  lazy val joins: Dataset[OpinionCitation] = RunCLOpinionProcessor.runJoins(cts, dkts, clstrs, opn, citations).apply().cache()

  "Joins" should "match snapshot" in {
    assertSnapshot("ComposedJoins", joins.toDF().coalesce(1), "opinion_id")
  }

  lazy val transforms: Dataset[OpinionOutboundCitations] = RunCLOpinionProcessor.runTransforms(AppParams.Environment.local, "", true)(joins).cache()

  "Transforms" should "match snapshot" in {
    assertSnapshot("ComposedTransforms", transforms.toDF().coalesce(1), "opinion_id")
  }

}
