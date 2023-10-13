package com.amicusearch.etl.process.courtlistener.joins

import com.amicusearch.etl.datatypes.courtlistener.citations.ParsedCitation
import com.amicusearch.etl.datatypes.courtlistener.clusters.ClusterWithNulls
import com.amicusearch.etl.datatypes.courtlistener.joins.{ClusterOpinion, DocketCluster, OpinionCitation}
import org.apache.spark.sql.{Dataset, SQLContext, SparkSession}

object OpinionsToCitations {
  def apply(citations: Dataset[ParsedCitation])(implicit spark: SparkSession, SQLContext: SQLContext):
  Dataset[ClusterOpinion] => Dataset[OpinionCitation] = opinions => {
    import SQLContext.implicits._
    opinions.joinWith(citations, opinions("cluster_id") === citations("cluster_id"), "left").map {
      case (o: ClusterOpinion, c: ParsedCitation) => OpinionCitation(
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
        date_filed = o.date_filed,
        scdb_id = o.scdb_id,
        headnotes = o.headnotes,
        summary = o.summary,
        citation_count = o.citation_count,
        precedential_status = o.precedential_status,
        opinion_id = o.opinion_id,
        plain_text = o.plain_text,
        volume = c.volume,
        reporter = c.reporter,
        page = c.page,
        cite_type = c.cite_type,
        cluster_id = o.cluster_id,
      )
    }
  }
}
