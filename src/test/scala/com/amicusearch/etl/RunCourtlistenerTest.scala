package com.amicusearch.etl

import org.scalatest.flatspec.AnyFlatSpec
import com.amicusearch.etl.RunCourtlistener
import com.amicusearch.etl.datatypes.courtlistener.citations.ParsedCitation
import com.amicusearch.etl.datatypes.courtlistener.clusters.ClusterWithNulls
import com.amicusearch.etl.datatypes.courtlistener.courts.Court
import com.amicusearch.etl.datatypes.courtlistener.dockets.DocketsWithNulls
import com.amicusearch.etl.datatypes.courtlistener.joins.OpinionCitation
import com.amicusearch.etl.datatypes.courtlistener.opinions.OpinionsCleanWhitespace
import com.amicusearch.etl.datatypes.courtlistener.transforms.OpinionDatePartition
import org.apache.spark.sql.Dataset

class RunCourtlistenerTest extends AnyFlatSpec with GenericAmicusearchTest {

  lazy val cts: Dataset[Court] = RunCourtlistener.processCourts("src/test/resources/courtlistener_courts_sample.jsonl",
    AppParams.Environment.local, List(), true).apply().cache()

  "Courts" should "match snapshot" in {
    assertSnapshot("ComposedCourts", cts.toDF().coalesce(1), "id")
  }

  lazy val dkts: Dataset[DocketsWithNulls] = RunCourtlistener.processDockets("src/test/resources/courtlistener_dockets_sample.jsonl",
    AppParams.Environment.local).apply().cache()

  "Dockets" should "match snapshot" in {
    assertSnapshot("ComposedDockets", dkts.toDF().coalesce(1), "id")
  }

  lazy val clstrs: Dataset[ClusterWithNulls] = RunCourtlistener.processClusters("src/test/resources/courtlistener_opinion_cluster_sample.jsonl",
    AppParams.Environment.local).apply().cache()

  "Clusters" should "match snapshot" in {
    assertSnapshot("ComposedClusters", clstrs.toDF().coalesce(1), "id")
  }

  lazy val opn: Dataset[OpinionsCleanWhitespace] = RunCourtlistener.processOpinions("src/test/resources/courtlistener_opinions_sample.jsonl",
    AppParams.Environment.local).apply().cache()

  "Opinions" should "match snapshot" in {
    assertSnapshot("ComposedOpinions", opn.toDF().coalesce(1), "id")
  }

  lazy val citations: Dataset[ParsedCitation] = RunCourtlistener.processCitations("src/test/resources/courtlistener_citations_sample.jsonl",
    AppParams.Environment.local).apply().cache()

  "Citations" should "match snapshot" in {
    assertSnapshot("ComposedCitations", citations.toDF().coalesce(1), "id")
  }

  lazy val joins: Dataset[OpinionCitation] = RunCourtlistener.runJoins(cts, dkts, clstrs, opn, citations).apply().cache()

  "Joins" should "match snapshot" in {
    assertSnapshot("ComposedJoins", joins.toDF().coalesce(1), "opinion_id")
  }

  lazy val transforms: Dataset[OpinionDatePartition] = RunCourtlistener.runTransforms(joins).cache()

  "Transforms" should "match snapshot" in {
    assertSnapshot("ComposedTransforms", transforms.toDF().coalesce(1), "opinion_id")
  }

}
