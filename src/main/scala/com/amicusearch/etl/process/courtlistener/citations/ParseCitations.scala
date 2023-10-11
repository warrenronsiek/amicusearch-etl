package com.amicusearch.etl.process.courtlistener.citations

import com.amicusearch.etl.datatypes.courtlistener.citations.ParsedCitation
import com.amicusearch.etl.datatypes.courtlistener.courts.Court
import com.amicusearch.etl.utils.Funcs
import org.apache.spark.sql.{DataFrame, Dataset, Row, SQLContext, SparkSession}

object ParseCitations {

  def apply()(implicit spark: SparkSession, SQLContext: SQLContext): DataFrame => Dataset[ParsedCitation] = df => {
    import SQLContext.implicits._
    df.map((r: Row) => ParsedCitation(
      id = r.getAs[String]("id"),
      volume = r.getAs[String]("volume"),
      reporter = r.getAs[String]("reporter"),
      page = r.getAs[String]("page"),
      cite_type = r.getAs[String]("citation_type"),
      cluster_id = r.getAs[String]("cluster_id"),
    ))
  }
}
