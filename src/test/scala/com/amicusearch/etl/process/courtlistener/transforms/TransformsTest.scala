package com.amicusearch.etl.process.courtlistener.transforms

import com.amicusearch.etl.GenericAmicusearchTest
import org.scalatest.flatspec.AnyFlatSpec

class TransformsTest extends AnyFlatSpec with GenericAmicusearchTest {

  "Transforms" should "compute ltrees" in {
    assertSnapshot("OpinionsLtree", courtLtree.toDF().coalesce(1), "opinion_id")
  }

  it should "compute date partitions" in {
    assertSnapshot("OpinionsDatePartition", datePartitions.toDF().coalesce(1), "opinion_id")
  }

}
