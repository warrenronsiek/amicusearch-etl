package com.amicusearch.etl.process.courtlistener.opinions

import com.amicusearch.etl.datatypes.courtlistener.OpinionsWithNulls
import org.apache.spark.sql.{DataFrame, Dataset, Row, SQLContext, SparkSession}

object ParseNulls {

  private def parseNan(s: String): Option[String] = s match {
    case "nan" => None
    case "" => None
    case s: String => Some(s)
    case _ => None
  }

  def apply()(implicit spark: SparkSession, SQLContext: SQLContext): DataFrame => Dataset[OpinionsWithNulls] = df => {
    import SQLContext.implicits._
    df.map((r: Row) => OpinionsWithNulls(
      id = r.getAs[String]("id"),
      plain_text = parseNan(r.getAs[String]("plain_text")),
      html = parseNan(r.getAs[String]("html")),
      html_anon_2020 = parseNan(r.getAs[String]("html_anon_2020")),
      xml_harvard = parseNan(r.getAs[String]("xml_harvard")),
      html_with_citations = parseNan(r.getAs[String]("html_with_citations")),
      cluster_id = parseNan(r.getAs[String]("cluster_id"))
    ))
  }
}
