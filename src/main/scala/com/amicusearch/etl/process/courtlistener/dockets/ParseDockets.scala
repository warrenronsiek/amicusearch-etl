package com.amicusearch.etl.process.courtlistener.dockets

import com.amicusearch.etl.datatypes.courtlistener.DocketsWithNulls
import org.apache.spark.sql.{DataFrame, Dataset, Row, SQLContext, SparkSession}
import com.amicusearch.etl.utils.Funcs

object ParseDockets {
  def apply()(implicit spark: SparkSession, SQLContext: SQLContext): DataFrame => Dataset[DocketsWithNulls] = df => {
    import SQLContext.implicits._
    df.map((r: Row) => DocketsWithNulls(
      id = r.getAs[String]("id"),
      date_created = r.getAs[String]("date_created"),
      date_modified = r.getAs[String]("date_modified"),
      source = r.getAs[String]("source"),
      appeal_from_str = Funcs.parseNan(r.getAs[String]("appeal_from_str")),
      assigned_to_str = Funcs.parseNan(r.getAs[String]("assigned_to_str")),
      referred_to_str = Funcs.parseNan(r.getAs[String]("referred_to_str")),
      panel_str = Funcs.parseNan(r.getAs[String]("panel_str")),
      date_last_index = r.getAs[String]("date_last_index"),
      date_cert_granted = Funcs.parseNan(r.getAs[String]("date_cert_granted")),
      date_cert_denied = Funcs.parseNan(r.getAs[String]("date_cert_denied")),
      date_argued = Funcs.parseNan(r.getAs[String]("date_argued")),
      date_reargued = Funcs.parseNan(r.getAs[String]("date_reargued")),
      date_reargument_denied = Funcs.parseNan(r.getAs[String]("date_reargument_denied")),
      date_filed = r.getAs[String]("date_filed"),
      date_terminated = Funcs.parseNan(r.getAs[String]("date_terminated")),
      date_last_filing = r.getAs[String]("date_last_filing"),
      case_name_short = Funcs.parseNan(r.getAs[String]("case_name_short")),
      case_name = r.getAs[String]("case_name"),
      case_name_full = Funcs.parseNan(r.getAs[String]("case_name_full")),
      slug = r.getAs[String]("slug"),
      docket_number = Funcs.parseNan(r.getAs[String]("docket_number")),
      docket_number_core = Funcs.parseNan(r.getAs[String]("docket_number_core")),
      pacer_case_id = r.getAs[String]("pacer_case_id"),
      cause = Funcs.parseNan(r.getAs[String]("cause")),
      nature_of_suit = Funcs.parseNan(r.getAs[String]("nature_of_suit")),
      jury_demand = Funcs.parseNan(r.getAs[String]("jury_demand")),
      jurisdiction_type = Funcs.parseNan(r.getAs[String]("jurisdiction_type")),
      appellate_fee_status = Funcs.parseNan(r.getAs[String]("appellate_fee_status")),
      appellate_case_type_information = Funcs.parseNan(r.getAs[String]("appellate_case_type_information")),
      mdl_status = Funcs.parseNan(r.getAs[String]("mdl_status")),
      filepath_local = Funcs.parseNan(r.getAs[String]("filepath_local")),
      filepath_ia = Funcs.parseNan(r.getAs[String]("filepath_ia")),
      filepath_ia_json = Funcs.parseNan(r.getAs[String]("filepath_ia_json")),
      ia_upload_failure_count = Funcs.parseNan(r.getAs[String]("ia_upload_failure_count")),
      ia_needs_upload = r.getAs[String]("ia_needs_upload"),
      ia_date_first_change = r.getAs[String]("ia_date_first_change"),
      view_count = r.getAs[String]("view_count"),
      date_blocked = r.getAs[String]("date_blocked"),
      blocked = r.getAs[String]("blocked"),
      appeal_from_id = Funcs.parseNan(r.getAs[String]("appeal_from_id")),
      assigned_to_id = Funcs.parseNan(r.getAs[String]("assigned_to_id")),
      court_id = r.getAs[String]("court_id"),
      idb_data_id = Funcs.parseNan(r.getAs[String]("idb_data_id")),
      originating_court_information_id = Funcs.parseNan(r.getAs[String]("originating_court_information_id")),
      referred_to_id = Funcs.parseNan(r.getAs[String]("referred_to_id"))
    ))
  }
}
