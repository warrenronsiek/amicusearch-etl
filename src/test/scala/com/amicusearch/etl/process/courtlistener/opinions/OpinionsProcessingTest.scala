package com.amicusearch.etl.process.courtlistener.opinions

import com.amicusearch.etl.GenericAmicusearchTest
import com.amicusearch.etl.datatypes.courtlistener.opinions.{OpinionsCleanWhitespace, OpinionsParsedHTML, OpinionsWithNulls}
import com.amicusearch.etl.process.courtlistener.opinions.RemoveTrivialOpinions
import com.amicusearch.etl.utils.NLPParser
import org.apache.spark.sql.Dataset

import scala.jdk.CollectionConverters._
import org.scalatest.flatspec.AnyFlatSpec
import edu.stanford.nlp.pipeline.CoreDocument

import scala.xml.Elem

class OpinionsProcessingTest extends AnyFlatSpec with GenericAmicusearchTest {
  "Trivial Opinion identification" should "identify short opinions" in {
    val parsed = NLPParser("This is a short opinion.")
    assert(RemoveTrivialOpinions.isTrivial(parsed.lemmaIterator))
  }

  "OpinionsProcessing" should "parse nulls" in {
    val df = opinionProcessedNulls().toDF().coalesce(1)
    assertSnapshot("ParseNulls", df, "id")
  }

  it should "parse html" in {
    val df = opinionParsedHtml().toDF().coalesce(1)
    assertSnapshot("ParseHTML", df, "id")
  }

  it should "parse whitespace" in {
    val df = opinionCleanedWhitespace().toDF().coalesce(1)
    assertSnapshot("ParseWhitespace", df, "id")
  }

  it should "remove trivial opinions" in {
    val df = opinionRemovedTrivial().toDF().coalesce(1)
    assertSnapshot("RemoveTrivialOpinions", df, "id")
  }
}
