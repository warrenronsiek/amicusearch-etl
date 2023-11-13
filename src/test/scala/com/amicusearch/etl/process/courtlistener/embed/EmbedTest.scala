package com.amicusearch.etl.process.courtlistener.embed

import com.amicusearch.etl.GenericAmicusearchTest
import com.amicusearch.etl.datatypes.courtlistener.embed.EmbeddedText
import org.apache.spark.sql.Dataset
import org.scalatest.flatspec.AnyFlatSpec

class EmbedTest extends AnyFlatSpec with GenericAmicusearchTest {

  val typeCasted: Dataset[EmbeddedText] = Embed(System.getenv("COHERE_API_KEY")).apply(processedOpinions.limit(1))

  "Embedding" should "embed opinions" in {
    // embeddings have minor numerical diffs each time, so cant be snapshotted
    assertSnapshot("Embedding", typeCasted.toDF().drop("embedding").coalesce(1), "text_id")
  }
}
