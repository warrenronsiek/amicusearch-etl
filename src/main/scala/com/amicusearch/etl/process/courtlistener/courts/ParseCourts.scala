package com.amicusearch.etl.process.courtlistener.courts

import com.amicusearch.etl.datatypes.courtlistener.Court
import org.apache.spark.sql.{DataFrame, Dataset, Row, SQLContext, SparkSession}
import com.amicusearch.etl.utils.Funcs

object ParseCourts {

  def apply()(implicit spark: SparkSession, SQLContext: SQLContext): DataFrame => Dataset[Court] = df => {
    import SQLContext.implicits._
    df.map((r: Row) => Court(
      id = r.getAs[String]("id"),
      pacer_court_id = Funcs.parseNan(r.getAs[String]("pacer_court_id")),
      pacer_has_rss_feed = Funcs.parseNan(r.getAs[String]("pacer_has_rss_feed")),
      pacer_rss_entry_types = Funcs.parseNan(r.getAs[String]("pacer_rss_entry_types")),
      fjc_court_id = Funcs.parseNan(r.getAs[String]("fjc_court_id")),
      date_modified = Funcs.parseNan(r.getAs[String]("date_modified")),
      in_use = Funcs.parseNan(r.getAs[String]("in_use")),
      has_opinion_scraper = Funcs.parseNan(r.getAs[String]("has_opinion_scraper")),
      has_oral_argument_scraper = Funcs.parseNan(r.getAs[String]("has_oral_argument_scraper")),
      position = Funcs.parseNan(r.getAs[String]("position")),
      citation_string = Funcs.parseNan(r.getAs[String]("citation_string")),
      short_name = Funcs.parseNan(r.getAs[String]("short_name")),
      full_name = Funcs.parseNan(r.getAs[String]("full_name")),
      url = Funcs.parseNan(r.getAs[String]("url")),
      start_date = Funcs.parseNan(r.getAs[String]("start_date")),
      end_date = Funcs.parseNan(r.getAs[String]("end_date")),
      jurisdiction = r.getAs[String]("jurisdiction"),
      notes = Funcs.parseNan(r.getAs[String]("notes"))
    ))
  }

}
