package com.amicusearch.etl.utils

import edu.stanford.nlp.ling.CoreLabel
import edu.stanford.nlp.pipeline.{CoreDocument, StanfordCoreNLP}

import java.util.Properties
import scala.jdk.CollectionConverters._

class NLPParser(text: String) {
  val props: Properties = new Properties();
  props.setProperty("annotators", "tokenize,ssplit,pos,lemma")
  props.setProperty("coref.algorithm", "neural")
  val pipeline: StanfordCoreNLP = new StanfordCoreNLP(props)
  val document: CoreDocument = new CoreDocument(text)
  pipeline.annotate(document)
  val punctuation: Set[String] = Set(",", ".", "!", "?", ";", ":", "(", ")", "[", "]", "{", "}", "-", "_", "+", "=",
    "@", "#", "$", "%", "^", "&", "*", "|", "/", "\\", "<", ">", "~", "`", "'", "\"", "\n", "\t", "\r")

  def tokenIterator: Iterator[CoreLabel] = document.tokens().asScala.iterator
  def lemmaIterator: Iterator[String] = tokenIterator.map(_.lemma()).map(_.toLowerCase).filter(!punctuation.contains(_))
  def sentenceIterator: Iterator[String] = document.sentences().asScala.iterator.map(_.text())
}

object NLPParser {
  def apply(text: String): NLPParser = new NLPParser(text)
}
