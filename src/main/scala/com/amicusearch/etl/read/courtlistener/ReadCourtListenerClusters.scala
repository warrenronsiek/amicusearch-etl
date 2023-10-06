package com.amicusearch.etl.read.courtlistener


import com.amicusearch.etl.AppParams
import org.apache.spark.sql.{DataFrame, SQLContext, SparkSession}
import org.apache.spark.sql.types._

object ReadCourtListenerClusters {

  val schema: StructType = StructType(Array(
    StructField("id", StringType, nullable = true),
    StructField("date_created", StringType, nullable = true),
    StructField("date_modified", StringType, nullable = true),
    StructField("judges", StringType, nullable = true),
    StructField("date_filed", StringType, nullable = true),
    StructField("date_filed_is_approximate", StringType, nullable = true),
    StructField("slug", StringType, nullable = true),
    StructField("case_name_short", StringType, nullable = true),
    StructField("case_name", StringType, nullable = true),
    StructField("case_name_full", StringType, nullable = true),
    StructField("scdb_id", StringType, nullable = true),
    StructField("scdb_decision_direction", StringType, nullable = true),
    StructField("scdb_votes_majority", StringType, nullable = true),
    StructField("scdb_votes_minority", StringType, nullable = true),
    StructField("source", StringType, nullable = true),
    StructField("procedural_history", StringType, nullable = true),
    StructField("attorneys", StringType, nullable = true),
    StructField("nature_of_suit", StringType, nullable = true),
    StructField("posture", StringType, nullable = true),
    StructField("syllabus", StringType, nullable = true),
    StructField("headnotes", StringType, nullable = true),
    StructField("summary", StringType, nullable = true),
    StructField("disposition", StringType, nullable = true),
    StructField("history", StringType, nullable = true),
    StructField("other_dates", StringType, nullable = true),
    StructField("cross_reference", StringType, nullable = true),
    StructField("correction", StringType, nullable = true),
    StructField("citation_count", StringType, nullable = true),
    StructField("precedential_status", StringType, nullable = true),
    StructField("date_blocked", StringType, nullable = true),
    StructField("blocked", StringType, nullable = true),
    StructField("filepath_json_harvard", StringType, nullable = true),
    StructField("docket_id", StringType, nullable = true),
    StructField("arguments", StringType, nullable = true),
    StructField("headmatter", StringType, nullable = true)
  ))

  def apply(path: String, env: AppParams.Environment.Value)(implicit spark: SparkSession): Unit => DataFrame = {
    GenericCourtlisterReader(path, env, schema)
  }
}
