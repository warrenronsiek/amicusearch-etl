package com.amicusearch.etl.read.courtlistener

import com.amicusearch.etl.GenericAmicusearchTest
import org.scalatest.flatspec.AnyFlatSpec

class ReadCourtlistenerTest extends AnyFlatSpec with GenericAmicusearchTest {

  "ReadCourtlistener" should "read the courtlistener citations" in {
    assertSnapshot("ReadCourtListenerCitations", courtListenerCitations().coalesce(1), "id")
  }

  it should "read the courtlistener courts" in {
    assertSnapshot("ReadCourtListenerCourts", courtListenerCourts().coalesce(1), "id")
  }

  it should "read the courtlistener opinions" in {
    assertSnapshot("ReadCourtListenerOpinions", courtListenerOpinions().coalesce(1), "id")
  }

  it should "read the courtlistener dockets" in {
    assertSnapshot("ReadCourtListenerDockets", courtListenerDockets().coalesce(1), "id")
  }

  it should "read the courtlistener clusters" in {
    assertSnapshot("ReadCourtListenerClusters", courtListenerClusters().coalesce(1), "id")
  }
}
