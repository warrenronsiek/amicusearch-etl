package com.amicusearch.etl.process.courtlistener.opinions

import com.amicusearch.etl.datatypes.courtlistener.opinions.{OpinionsCleanWhitespace, OpinionsParsedHTML}
import com.amicusearch.etl.utils.NLPParser
import com.typesafe.scalalogging.LazyLogging
import edu.stanford.nlp.ling.CoreLabel
import edu.stanford.nlp.pipeline.{CoreDocument, StanfordCoreNLP}
import org.apache.spark.sql.{Dataset, SQLContext, SparkSession}

import java.util.Properties
import scala.annotation.tailrec

object RemoveTrivialOpinions extends LazyLogging{

  case class TrivialStats(tokensScanned: Int = 0, trivialTokens: Int = 0, tokenSet: Set[String] = Set()) {
    private val trivialTokenSet: Set[String] = Set(
      "motion", "affirm", "deny", "dismiss", "suspend", "granted", "pauperis", "certiorari", "disbar", "resign",
      "application", "pawperis", "solicitor", "curiam", "consolidate", "appeal", "discharge", "amicus", "curiae")

    def isShort: Boolean = tokensScanned < 50
    def tooManyTrivialTokens: Boolean = trivialTokens > 2
    def tooFewDistinctTokens: Boolean = tokenSet.size < 20

    def isTrivial: Boolean = isShort || tooManyTrivialTokens || tooFewDistinctTokens

    def addToken(token: String): TrivialStats = {
      if (trivialTokenSet.contains(token)) {
        TrivialStats(tokensScanned + 1, trivialTokens + 1, tokenSet + token)
      } else {
        TrivialStats(tokensScanned + 1, trivialTokens, tokenSet + token)
      }
    }
  }

  @tailrec
  def isTrivial(docIter: Iterator[String], trivialStats: TrivialStats = TrivialStats()): Boolean = {
    if (docIter.hasNext && trivialStats.isShort) {
      val token = docIter.next()
      isTrivial(docIter, trivialStats.addToken(token))
    } else {
      trivialStats.isTrivial
    }
  }

  def apply()(implicit spark: SparkSession, SQLContext: SQLContext): Dataset[OpinionsCleanWhitespace] => Dataset[OpinionsCleanWhitespace] = ds => {
    import SQLContext.implicits._
    ds.filter((r: OpinionsCleanWhitespace) => {
        r.plain_text match {
          case Some(s: String) => s.length > 100
          case None => false
        }
      })
      .filter(r => r.plain_text.nonEmpty)
      .filter(r => {
        val parsed = NLPParser(r.plain_text.get)
        !isTrivial(parsed.lemmaIterator)
      })
  }
}
