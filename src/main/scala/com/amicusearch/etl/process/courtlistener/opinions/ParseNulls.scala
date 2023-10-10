package com.amicusearch.etl.process.courtlistener.opinions

import com.amicusearch.etl.datatypes.courtlistener.opinions.OpinionsWithNulls
import org.apache.spark.sql.{DataFrame, Dataset, Row, SQLContext, SparkSession}
import com.amicusearch.etl.utils.Funcs

object ParseNulls {

  def apply()(implicit spark: SparkSession, SQLContext: SQLContext): DataFrame => Dataset[OpinionsWithNulls] = df => {
    import SQLContext.implicits._
    df.map((r: Row) => OpinionsWithNulls(
      id = r.getAs[String]("id"),
      plain_text = Funcs.parseNan(r.getAs[String]("plain_text")),
      html = Funcs.parseNan(r.getAs[String]("html")),
      html_anon_2020 = Funcs.parseNan(r.getAs[String]("html_anon_2020")),
      xml_harvard = Funcs.parseNan(r.getAs[String]("xml_harvard")),
      html_with_citations = Funcs.parseNan(r.getAs[String]("html_with_citations")),
      cluster_id = Funcs.parseNan(r.getAs[String]("cluster_id"))
    ))
  }
}
