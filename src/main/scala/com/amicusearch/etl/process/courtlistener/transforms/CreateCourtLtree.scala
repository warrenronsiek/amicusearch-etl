package com.amicusearch.etl.process.courtlistener.transforms

import com.amicusearch.etl.datatypes.courtlistener.joins.OpinionCitation
import com.amicusearch.etl.datatypes.courtlistener.transforms.OpinionLtree
import org.apache.spark.sql.{Dataset, SQLContext, SparkSession}
import com.amicusearch.etl.utils.CourtStructure
object CreateCourtLtree {

  def apply()(implicit spark: SparkSession, SQLContext: SQLContext): Dataset[OpinionCitation] => Dataset[OpinionLtree] = opinions => {
    import SQLContext.implicits._
    opinions.map(o => OpinionLtree(
      court_id = o.court_id,
      court_citation_string = o.court_citation_string,
      court_short_name = o.court_short_name,
      court_full_name = o.court_full_name,
      docket_id = o.docket_id,
      docket_number = o.docket_number,
      docket_number_core = o.docket_number_core,
      case_name_short = o.case_name_short,
      case_name = o.case_name,
      case_name_full = o.case_name_full,
      slug = o.slug,
      cluster_id = o.cluster_id,
      date_filed = o.date_filed,
      scdb_id = o.scdb_id,
      headnotes = o.headnotes,
      summary = o.summary,
      citation_count = o.citation_count,
      precedential_status = o.precedential_status,
      opinion_id = o.opinion_id,
      plain_text = o.plain_text,
      volume = o.volume,
      reporter = o.reporter,
      page = o.page,
      cite_type = o.cite_type,
      ltree = o.court_full_name match {
        case Some(s) => CourtStructure.getCourtLtree(s)
        case None => None
      }
    ))
  }

}
