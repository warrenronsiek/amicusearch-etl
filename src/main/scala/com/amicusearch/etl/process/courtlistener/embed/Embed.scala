package com.amicusearch.etl.process.courtlistener.embed

import com.amicusearch.etl.datatypes.courtlistener.embed.EmbeddedText
import com.amicusearch.etl.datatypes.courtlistener.transforms.OpinionSummary
import com.amicusearch.etl.utils.{NLPParser, OpenAIEmbedder}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SQLContext, SparkSession}
import java.security.MessageDigest

object Embed {

  def md5(s: String): String = {
    MessageDigest.getInstance("MD5").digest(s.getBytes).map("%02x".format(_)).mkString
  }

  def apply()(implicit spark: SparkSession, SQLContext: SQLContext)
  : Dataset[Row] => Dataset[EmbeddedText] = ds => {
    import SQLContext.implicits._
    ds.flatMap(r => {
      NLPParser(r.getAs[String]("plain_text")).sentenceBlockIterator(3).map(sentenceBlock => EmbeddedText(
        region_partition = r.getAs[String]("region_partition"),
        date_filed = r.getAs[String]("date_filed"),
        citation_count = r.getAs[String]("citation_count"),
        precedential_status = r.getAs[String]("precedential_status"),
        opinion_id = r.getAs[String]("opinion_id"),
        ltree = r.getAs[String]("ltree"),
        text = sentenceBlock,
        text_id = md5(sentenceBlock),
        embedding = OpenAIEmbedder.embed(sentenceBlock)
      ))
    })
  }
}
