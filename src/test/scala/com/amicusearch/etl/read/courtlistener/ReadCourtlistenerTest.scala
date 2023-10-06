package com.amicusearch.etl.read.courtlistener

import com.amicusearch.etl.GenericAmicusearchTest
import org.scalatest.flatspec.AnyFlatSpec

class ReadCourtlistenerTest extends AnyFlatSpec with GenericAmicusearchTest {

  "ReadCourtlistener" should "read the courtlistener citations" in {
    val df = ReadCourtListenerCitations(getResourcePath("courtlistener_citations_sample.jsonl"), casetextPartitionParams.env)(sparkSession)()
    assertSnapshot("ReadCourtListenerCitations", df.coalesce(1), "id")
  }

  it should "read the courtlistener courts" in {
    val df = ReadCourtListenerCourts(getResourcePath("courtlistener_courts_sample.jsonl"), casetextPartitionParams.env)(sparkSession)()
    assertSnapshot("ReadCourtListenerCourts", df.coalesce(1), "id")
  }

  it should "read the courtlistener opinions" in {
    val df = ReadCourtListenerOpinions(getResourcePath("courtlistener_opinions_sample.jsonl"), casetextPartitionParams.env)(sparkSession)()
    assertSnapshot("ReadCourtListenerOpinions", df.coalesce(1), "id")
  }

  it should "read the courtlistener dockets" in {
    val df = ReadCourtListenerDockets(getResourcePath("courtlistener_dockets_sample.jsonl"), casetextPartitionParams.env)(sparkSession)()
    assertSnapshot("ReadCourtListenerDockets", df.coalesce(1), "id")
  }

  it should "read the courtlistener clusters" in {
    val df = ReadCourtListenerClusters(getResourcePath("courtlistener_opinion_cluster_sample.jsonl"), casetextPartitionParams.env)(sparkSession)()
    assertSnapshot("ReadCourtListenerClusters", df.coalesce(1), "id")
  }
}
