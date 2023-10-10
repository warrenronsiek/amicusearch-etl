package com.amicusearch.etl.process.courtlistener.dockets

import com.amicusearch.etl.datatypes.courtlistener.DocketsWithNulls
import org.apache.spark.sql.{DataFrame, Dataset, Row, SQLContext, SparkSession}
import com.amicusearch.etl.utils.Funcs

object ParseDockets {
  def apply()(implicit spark: SparkSession, SQLContext: SQLContext): DataFrame => Dataset[DocketsWithNulls] = df => {
    import SQLContext.implicits._
    df.select("id", "case_name_short", "case_name", "case_name_full", "slug", "docket_number", "docket_number_core", "court_id")
      .map((r: Row) => DocketsWithNulls(
        id = r.getAs[String]("id"),
        case_name_short = Funcs.parseNan(r.getAs[String]("case_name_short")),
        case_name = r.getAs[String]("case_name"),
        case_name_full = Funcs.parseNan(r.getAs[String]("case_name_full")),
        slug = r.getAs[String]("slug"),
        docket_number = Funcs.parseNan(r.getAs[String]("docket_number")),
        docket_number_core = Funcs.parseNan(r.getAs[String]("docket_number_core")),
        court_id = r.getAs[String]("court_id")
      ))
  }
}
