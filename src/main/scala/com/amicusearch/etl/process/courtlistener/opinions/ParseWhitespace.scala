package com.amicusearch.etl.process.courtlistener.opinions

import com.amicusearch.etl.datatypes.courtlistener.{OpinionsParsedHTML, OpinionsCleanWhitespace}
import org.apache.spark.sql.{Dataset, SQLContext, SparkSession}

object ParseWhitespace {
  def apply()(implicit spark: SparkSession, SQLContext: SQLContext): Dataset[OpinionsParsedHTML] => Dataset[OpinionsCleanWhitespace] = ds => {
    import SQLContext.implicits._
    ds.map((r: OpinionsParsedHTML) => {
      OpinionsCleanWhitespace(
        id = r.id,
        plain_text = r.plain_text match {
          case Some(s: String) => Some(s.replaceAll("\\s+", " "))
          case None => None
        },
        cluster_id = r.cluster_id
      )
    })
  }
}
