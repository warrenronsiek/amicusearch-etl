package com.amicusearch.etl.process.courtlistener.embed

import com.amicusearch.etl.datatypes.courtlistener.embed.EmbeddedText
import com.amicusearch.etl.utils.{CohereEmbedder, NLPParser}
import org.apache.spark.sql.{Dataset, Row, SQLContext, SparkSession}

import java.security.MessageDigest

object Embed {

  def md5(s: String): String = {
    MessageDigest.getInstance("MD5").digest(s.getBytes).map("%02x".format(_)).mkString
  }

  def apply(cohereKey: String)(implicit spark: SparkSession, SQLContext: SQLContext)
  : Dataset[Row] => Dataset[EmbeddedText] = ds => {
    import SQLContext.implicits._
    val embedder = new CohereEmbedder(cohereKey)
    ds.flatMap(r => {
      NLPParser(r.getAs[String]("plain_text"))
        .sentenceBlockIterator(3)
        .grouped(96)
        .map(groupBlock => embedder.embed(groupBlock))
        .flatMap(embeddedBlock => embeddedBlock.map(embedding => EmbeddedText(
          date_filed = r.getAs[String]("date_filed"),
          citation_count = r.getAs[String]("citation_count"),
          precedential_status = r.getAs[String]("precedential_status"),
          opinion_id = r.getAs[String]("opinion_id"),
          ltree = r.getAs[String]("ltree"),
          text = embedding._1,
          text_id = md5(embedding._1),
          embedding = embedding._2,
        )))
    })
  }
}
