package com.amicusearch.etl.utils

import org.scalatest.flatspec.AnyFlatSpec

class NLPParserTest extends AnyFlatSpec {
  "NLPParser" should "create token iterators" in {
    val parser = NLPParser("This is a short opinion.")
    val tokens = parser.tokenIterator.toList.map(_.word())
    assert(tokens == List("This", "is", "a", "short", "opinion", "."))
  }

  it should "create sentence iterators" in {
    val parser = NLPParser("This is a short opinion. This is a longer opinion.")
    val sentences = parser.sentenceIterator.toList
    assert(sentences == List("This is a short opinion.", "This is a longer opinion."))
  }

  it should "create lemma iterators" in {
    val parser = NLPParser("This is a short opinion.")
    val lemmas = parser.lemmaIterator.toList
    assert(lemmas == List("this", "be", "a", "short", "opinion"))
  }
}
