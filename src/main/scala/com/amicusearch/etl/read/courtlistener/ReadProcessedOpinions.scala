package com.amicusearch.etl.read.courtlistener

import com.amicusearch.etl.AppParams
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.types.{StructField, StructType}
import org.apache.spark.sql.types._

object ReadProcessedOpinions {

  val schema: StructType = StructType(Array(
    StructField("court_id", StringType, nullable = true),
    StructField("court_citation_string", StringType, nullable = true),
    StructField("court_short_name", StringType, nullable = true),
    StructField("court_full_name", StringType, nullable = true),
    StructField("docket_id", StringType, nullable = true),
    StructField("docket_number", StringType, nullable = true),
    StructField("docket_number_core", StringType, nullable = true),
    StructField("case_name_short", StringType, nullable = true),
    StructField("case_name", StringType, nullable = true),
    StructField("case_name_full", StringType, nullable = true),
    StructField("slug", StringType, nullable = true),
    StructField("region_partition", StringType, nullable = true),
    StructField("cluster_id", StringType, nullable = true),
    StructField("date_filed", StringType, nullable = true),
    StructField("scdb_id", StringType, nullable = true),
    StructField("headnotes", StringType, nullable = true),
    StructField("summary", StringType, nullable = true),
    StructField("citation_count", StringType, nullable = true),
    StructField("precedential_status", StringType, nullable = true),
    StructField("opinion_id", StringType, nullable = true),
    StructField("plain_text", StringType, nullable = true),
    StructField("volume", StringType, nullable = true),
    StructField("reporter", StringType, nullable = true),
    StructField("page", StringType, nullable = true),
    StructField("cite_type", StringType, nullable = true),
    StructField("ltree", StringType, nullable = true),
    StructField("date_partition", StringType, nullable = true),
    StructField("generated_summary", StringType, nullable = true)
  ))

  def apply(path: String, env: AppParams.Environment.Value)(implicit spark: SparkSession): Unit => DataFrame = {
    GenericCourtlisterReader(path, env, schema)
  }
}
