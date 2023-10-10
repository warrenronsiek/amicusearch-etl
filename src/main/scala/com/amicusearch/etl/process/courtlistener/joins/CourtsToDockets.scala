package com.amicusearch.etl.process.courtlistener.joins

import com.amicusearch.etl.datatypes.courtlistener.courts.Court
import com.amicusearch.etl.datatypes.courtlistener.{CourtDocket, DocketsWithNulls}
import org.apache.spark.sql.{Dataset, SQLContext, SparkSession}

object CourtsToDockets {

  def apply(courts: Dataset[Court])(implicit spark: SparkSession, SQLContext: SQLContext): Dataset[DocketsWithNulls] => Dataset[CourtDocket] =
    dockets => {
      import SQLContext.implicits._
      dockets.joinWith(courts, courts("id") === dockets("court_id"), "inner").map {
        case (d: DocketsWithNulls, c: Court) => CourtDocket(
          court_id = c.id,
          court_citation_string = c.citation_string,
          court_short_name = c.short_name,
          court_full_name = c.full_name,
          docket_id = d.id,
          docket_number = d.docket_number,
          docket_number_core = d.docket_number_core,
          case_name_short = d.case_name_short,
          case_name = d.case_name,
          case_name_full = d.case_name_full,
          slug = d.slug
        )
      }
    }

}
