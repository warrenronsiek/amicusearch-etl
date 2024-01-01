package com.amicusearch.etl.process.courtlistener.transforms

import com.amicusearch.etl.AppParams
import com.amicusearch.etl.datatypes.courtlistener.transforms.{OpinionOutboundCitations, OpinionSummary}
import com.amicusearch.etl.utils.MLServerGetCitations
import org.apache.spark.sql.{Dataset, SQLContext, SparkSession}

object CreateOutboundCitations {

  def apply(env: AppParams.Environment.Value, getCitationsUrl: String)
           (implicit spark: SparkSession, SQLContext: SQLContext): Dataset[OpinionSummary] => Dataset[OpinionOutboundCitations] = opinions => {
    import SQLContext.implicits._
    val getCites = MLServerGetCitations(env, getCitationsUrl)
    opinions.map(o => OpinionOutboundCitations(
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
      region_partition = o.region_partition,
      cluster_id = o.cluster_id,
      date_filed = o.date_filed,
      scdb_id = o.scdb_id,
      headnotes = o.headnotes,
      summary = o.summary,
      citation_count = o.citation_count,
      precedential_status = o.precedential_status,
      opinion_id = o.opinion_id,
      plain_text = o.plain_text,
      citations = o.citations,
      ltree = o.ltree,
      generated_summary = o.generated_summary,
      outbound_citations = getCites.getCitations(o.plain_text.getOrElse("")).toArray
    ))
  }
}