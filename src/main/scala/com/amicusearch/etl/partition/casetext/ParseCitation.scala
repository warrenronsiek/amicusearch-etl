package com.amicusearch.etl.partition.casetext


import com.amicusearch.etl.datatypes.CasetextCase
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.apache.spark.sql.{DataFrame, Dataset, Row, SQLContext, SparkSession}
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser

object ParseCitation {

  def parseCitation(citationHTML: Option[String]): Option[String] = {
    citationHTML match {
      case Some(value) =>
        val doc = JsoupBrowser().parseString(value.replace("\\/", "/"))
        (doc >> "div" map (_.ownText)).headOption
      case None => None
    }
  }

  def apply()(implicit spark: SparkSession, SQLContext: SQLContext):  Dataset[CasetextCase] => Dataset[CasetextCase] = df => {
    import SQLContext.implicits._

    df.map((c: CasetextCase) => CasetextCase(
      document_type = c.document_type,
      document = c.document,
      title = c.title,
      court = c.court,
      date = c.date,
      citation = parseCitation(c.citation),
      url = c.url))
  }
}
