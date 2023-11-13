package com.amicusearch.etl.utils

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CohereEmbedderTest extends AnyFlatSpec with Matchers {

  "CohereEmbedder" should "embed a string" in {
    val embedding = CohereEmbedder.embed("This is a short opinion.")
    assert(embedding.length == 1024)
  }

  it should "embed strings with nested quotations" in {
    val embedding = CohereEmbedder.embed("This is a short opinion. \"This is a longer opinion.\"")
    assert(embedding.length == 1024)
  }

  it should "embed arrays of strings" in {
    val embedding = CohereEmbedder.embed(Array("This is a short opinion.", "This is a longer opinion."))
    embedding.length should be (2)
    embedding(0)._1 should be ("this is a short opinion.")
    embedding(0)._2.length should be (1024)
  }
}
