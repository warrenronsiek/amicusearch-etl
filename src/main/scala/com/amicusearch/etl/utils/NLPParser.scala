package com.amicusearch.etl.utils

import edu.stanford.nlp.ling.CoreLabel
import edu.stanford.nlp.pipeline.{CoreDocument, StanfordCoreNLP}

import java.util.Properties
import scala.collection.JavaConverters._

class NLPParser(text: String) {
  private val props: Properties = new Properties()
  props.setProperty("annotators", "tokenize,ssplit,pos,lemma")
  props.setProperty("coref.algorithm", "statistical")
  private val pipeline: StanfordCoreNLP = new StanfordCoreNLP(props)
  private val document: CoreDocument = new CoreDocument(text)
  pipeline.annotate(document)
  private val punctuation: Set[String] = Set(",", ".", "!", "?", ";", ":", "(", ")", "[", "]", "{", "}", "-", "_", "+",
    "=", "@", "#", "$", "%", "^", "&", "*", "|", "/", "\\", "<", ">", "~", "`", "'", "\"", "\n", "\t", "\r")

  lazy val tokenIterator: Iterator[CoreLabel] = document.tokens().asScala.iterator
  lazy val lemmaIterator: Iterator[String] = tokenIterator.map(_.lemma()).map(_.toLowerCase).filter(!punctuation.contains(_))
  lazy val sentenceIterator: Iterator[String] = document.sentences().asScala.iterator.map(_.text())

  def sentenceBlockIterator(blockSize: Int): Iterator[String] = {
    val sentenceIter = sentenceIterator
    new Iterator[String] {
      override def hasNext: Boolean = sentenceIter.hasNext

      override def next(): String = {
        val sb = new StringBuilder()
        var i = 0
        while (i < blockSize && sentenceIter.hasNext) {
          sb.append(sentenceIter.next())
          sb.append(" ")
          i += 1
        }
        sb.toString().trim
      }
    }
  }
}

object NLPParser {
  def apply(text: String): NLPParser = new NLPParser(text)
}
