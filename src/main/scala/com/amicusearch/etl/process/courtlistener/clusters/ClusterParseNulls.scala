package com.amicusearch.etl.process.courtlistener.clusters

import com.amicusearch.etl.datatypes.courtlistener.{ClusterWithNulls, OpinionsWithNulls}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SQLContext, SparkSession}
import com.amicusearch.etl.utils.Funcs

object ClusterParseNulls {

  def apply()(implicit spark: SparkSession, SQLContext: SQLContext): DataFrame => Dataset[ClusterWithNulls] = ds => {
    import SQLContext.implicits._
    ds.select("id", "date_filed", "scdb_id", "headnotes", "summary", "citation_count", "precedential_status", "docket_id")
      .map((r: Row) => {
        ClusterWithNulls(
          id = r.getAs[String]("id"),
          date_filed = r.getAs[String]("date_filed"),
          scdb_id = Funcs.parseNan(r.getAs[String]("scdb_id")),
          headnotes = Funcs.parseNan(r.getAs[String]("headnotes")),
          summary = Funcs.parseNan(r.getAs[String]("summary")),
          citation_count = Funcs.parseNan(r.getAs[String]("citation_count")),
          precedential_status = Funcs.parseNan(r.getAs[String]("precedential_status")),
          docket_id = Funcs.parseNan(r.getAs[String]("docket_id")),
        )
      })
  }
}
