package com.amicusearch.etl.process.courtlistener.opinions

import com.amicusearch.etl.datatypes.courtlistener.{OpinionsParsedHTML, OpinionsWithNulls}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.apache.spark.sql.{DataFrame, Dataset, SQLContext, SparkSession}

import scala.xml.XML
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._

object ParseHTML {

  def apply()(implicit spark: SparkSession, SQLContext: SQLContext): Dataset[OpinionsWithNulls] => Dataset[OpinionsParsedHTML] = ds => {
    import SQLContext.implicits._
    ds.map((r: OpinionsWithNulls) => {

      lazy val parsed: Option[String] = List(r.html, r.html_anon_2020, r.html_with_citations, r.xml_harvard)
        .collectFirst({ case Some(s: String) =>
          val doc = JsoupBrowser().parseString(s.replace("\\/", "/"))
          (doc >> texts).mkString("\u00B6")
        })
      val plainText: Option[String] = r.plain_text match {
        case Some(s: String) => Some(s)
        case None => parsed
      }

      OpinionsParsedHTML(
        id = r.id,
        plain_text = plainText,
        cluster_id = r.cluster_id
      )
    })
  }
}
