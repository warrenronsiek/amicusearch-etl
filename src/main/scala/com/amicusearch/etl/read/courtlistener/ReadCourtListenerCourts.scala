package com.amicusearch.etl.read.courtlistener

import com.amicusearch.etl.AppParams
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, SparkSession}

object ReadCourtListenerCourts {

  val schema: StructType = StructType(Array(
    StructField("id", StringType, nullable = true),
    StructField("pacer_court_id", StringType, nullable = true),
    StructField("pacer_has_rss_feed", StringType, nullable = true),
    StructField("pacer_rss_entry_types", StringType, nullable = true),
    StructField("fjc_court_id", StringType, nullable = true),
    StructField("date_modified", StringType, nullable = true),
    StructField("in_use", StringType, nullable = true),
    StructField("has_opinion_scraper", StringType, nullable = true),
    StructField("has_oral_argument_scraper", StringType, nullable = true),
    StructField("position", StringType, nullable = true),
    StructField("citation_string", StringType, nullable = true),
    StructField("short_name", StringType, nullable = true),
    StructField("full_name", StringType, nullable = true),
    StructField("url", StringType, nullable = true),
    StructField("start_date", StringType, nullable = true),
    StructField("end_date", StringType, nullable = true),
    StructField("jurisdiction", StringType, nullable = true),
    StructField("notes", StringType, nullable = true)
  ))

  def apply(path: String, env: AppParams.Environment.Value)(implicit spark: SparkSession): Unit => DataFrame = {
    GenericCourtlisterReader(path, env, schema)
  }

}
