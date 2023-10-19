package com.amicusearch.etl.utils

import org.scalatest.flatspec.AnyFlatSpec

class OpenAIEmbedderTest extends AnyFlatSpec{

  "OpenAIEmbedder" should "embed a string" in {
    val embedding = OpenAIEmbedder.embed("This is a short opinion.")
    assert(embedding.length == 1536)
  }

  it should "embed strings with nested quotations" in {
    val embedding = OpenAIEmbedder.embed("This is a short opinion. \"This is a longer opinion.\"")
    assert(embedding.length == 1536)
  }

}
