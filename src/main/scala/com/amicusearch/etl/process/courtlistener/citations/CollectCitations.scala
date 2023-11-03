package com.amicusearch.etl.process.courtlistener.citations

import com.amicusearch.etl.datatypes.courtlistener.citations.{CollectedCitation, ConcatedCitation, ParsedCitation}
import org.apache.spark.sql.functions.collect_list
import org.apache.spark.sql.{Dataset, Row, SQLContext, SparkSession}

object CollectCitations {
  def apply()(implicit spark: SparkSession, SQLContext: SQLContext): Dataset[ConcatedCitation] => Dataset[CollectedCitation] = df => {
    import SQLContext.implicits._
    df.groupBy('cluster_id)
      .agg(collect_list('citation).as("citations"))
      .as[CollectedCitation]
  }
}
