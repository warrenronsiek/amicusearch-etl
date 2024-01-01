package com.amicusearch.etl.process.courtlistener.citations

import com.amicusearch.etl.datatypes.courtlistener.citations.{ConcatedCitation, ParsedCitation}
import org.apache.spark.sql.{DataFrame, Dataset, SQLContext, SparkSession}

object ConcatCitations {

  def apply()(implicit spark: SparkSession, SQLContext: SQLContext): Dataset[ParsedCitation] => Dataset[ConcatedCitation] = df => {
    import SQLContext.implicits._
    df.map((r: ParsedCitation) => ConcatedCitation(
      id = r.id,
      citation =   r.volume + " " + r.reporter + " " + r.page,
      cluster_id = r.cluster_id
    ))
  }
}
