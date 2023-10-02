package com.amicusearch.etl.partition.casetext

import com.amicusearch.etl.datatypes.CasetextCase
import org.apache.spark.sql.{DataFrame, Dataset, Row, SQLContext, SparkSession}
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser

object ParseTitle {

  def parseTitle(titleHTML:String): Option[String] = {
    val doc = JsoupBrowser().parseString(titleHTML)
    (doc >> "p" map (_.ownText)).headOption
  }

  def apply()(implicit spark: SparkSession, SQLContext: SQLContext): DataFrame => Dataset[CasetextCase] = df => {
    import SQLContext.implicits._

    df.map((r:Row) => CasetextCase(
      document_type = r.getAs[String]("type"),
      document = Some(r.getAs[String]("document")),
      title = parseTitle(r.getAs[String]("title")),
      court = Some(r.getAs[String]("court")),
      date = Some(r.getAs[String]("date")),
      citation = Some(r.getAs[String]("citation")),
      url = r.getAs[String]("url")
    ))
  }

}
