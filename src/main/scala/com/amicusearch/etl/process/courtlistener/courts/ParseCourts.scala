package com.amicusearch.etl.process.courtlistener.courts

import com.amicusearch.etl.datatypes.courtlistener.courts.Court
import org.apache.spark.sql.{DataFrame, Dataset, Row, SQLContext, SparkSession}
import com.amicusearch.etl.utils.Funcs

object ParseCourts {

  def apply()(implicit spark: SparkSession, SQLContext: SQLContext): DataFrame => Dataset[Court] = df => {
    import SQLContext.implicits._
    df.select("id", "citation_string","short_name", "full_name", "jurisdiction")
      .map((r: Row) => Court(
      id = r.getAs[String]("id"),
      citation_string = Funcs.parseNan(r.getAs[String]("citation_string")),
      short_name = Funcs.parseNan(r.getAs[String]("short_name")),
      full_name = Funcs.parseNan(r.getAs[String]("full_name")),
      jurisdiction = r.getAs[String]("jurisdiction"),
    ))
  }

}
