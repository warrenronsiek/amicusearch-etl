package com.amicusearch.etl.read.courtlistener

import com.amicusearch.etl.AppParams
import org.apache.spark.sql.{DataFrame, SQLContext, SparkSession}
import org.apache.spark.sql.types._

object ReadCourtListenerDockets {

  val schema: StructType = StructType(Array(
    StructField("id", StringType, nullable = true),
    StructField("date_created", StringType, nullable = true),
    StructField("date_modified", StringType, nullable = true),
    StructField("source", StringType, nullable = true),
    StructField("appeal_from_str", StringType, nullable = true),
    StructField("assigned_to_str", StringType, nullable = true),
    StructField("referred_to_str", StringType, nullable = true),
    StructField("panel_str", StringType, nullable = true),
    StructField("date_last_index", StringType, nullable = true),
    StructField("date_cert_granted", StringType, nullable = true),
    StructField("date_cert_denied", StringType, nullable = true),
    StructField("date_argued", StringType, nullable = true),
    StructField("date_reargued", StringType, nullable = true),
    StructField("date_reargument_denied", StringType, nullable = true),
    StructField("date_filed", StringType, nullable = true),
    StructField("date_terminated", StringType, nullable = true),
    StructField("date_last_filing", StringType, nullable = true),
    StructField("case_name_short", StringType, nullable = true),
    StructField("case_name", StringType, nullable = true),
    StructField("case_name_full", StringType, nullable = true),
    StructField("slug", StringType, nullable = true),
    StructField("docket_number", StringType, nullable = true),
    StructField("docket_number_core", StringType, nullable = true),
    StructField("pacer_case_id", StringType, nullable = true),
    StructField("cause", StringType, nullable = true),
    StructField("nature_of_suit", StringType, nullable = true),
    StructField("jury_demand", StringType, nullable = true),
    StructField("jurisdiction_type", StringType, nullable = true),
    StructField("appellate_fee_status", StringType, nullable = true),
    StructField("appellate_case_type_information", StringType, nullable = true),
    StructField("mdl_status", StringType, nullable = true),
    StructField("filepath_local", StringType, nullable = true),
    StructField("filepath_ia", StringType, nullable = true),
    StructField("filepath_ia_json", StringType, nullable = true),
    StructField("ia_upload_failure_count", StringType, nullable = true),
    StructField("ia_needs_upload", StringType, nullable = true),
    StructField("ia_date_first_change", StringType, nullable = true),
    StructField("view_count", StringType, nullable = true),
    StructField("date_blocked", StringType, nullable = true),
    StructField("blocked", StringType, nullable = true),
    StructField("appeal_from_id", StringType, nullable = true),
    StructField("assigned_to_id", StringType, nullable = true),
    StructField("court_id", StringType, nullable = true),
    StructField("idb_data_id", StringType, nullable = true),
    StructField("originating_court_information_id", StringType, nullable = true),
    StructField("referred_to_id", StringType, nullable = true)
  ))

  def apply(path: String, env: AppParams.Environment.Value)(implicit spark: SparkSession): Unit => DataFrame = {
    GenericCourtlisterReader(path, env, schema)
  }

}
