package com.amicusearch.etl.process.courtlistener.joins

import com.amicusearch.etl.datatypes.courtlistener.clusters.ClusterWithNulls
import com.amicusearch.etl.datatypes.courtlistener.joins.{ClusterOpinion, CourtDocket, DocketCluster}
import com.amicusearch.etl.datatypes.courtlistener.opinions.OpinionsCleanWhitespace
import org.apache.spark.sql.{Dataset, SQLContext, SparkSession}

object ClustersToOpinions {
  def apply(clusters: Dataset[DocketCluster])(implicit spark: SparkSession, SQLContext: SQLContext): Dataset[OpinionsCleanWhitespace] => Dataset[ClusterOpinion] =
    opinions => {
      import SQLContext.implicits._
      opinions.joinWith(clusters, opinions("cluster_id") === clusters("cluster_id"), "inner").map {
        case (o:OpinionsCleanWhitespace, c:DocketCluster) => ClusterOpinion(
          court_id = c.court_id,
          court_citation_string = c.court_citation_string,
          court_short_name = c.court_short_name,
          court_full_name = c.court_full_name,
          docket_id = c.docket_id,
          docket_number = c.docket_number,
          docket_number_core = c.docket_number_core,
          case_name_short = c.case_name_short,
          case_name = c.case_name,
          case_name_full = c.case_name_full,
          slug = c.slug,
          cluster_id = c.cluster_id,
          date_filed = c.date_filed,
          scdb_id = c.scdb_id,
          headnotes = c.headnotes,
          summary = c.summary,
          citation_count = c.citation_count,
          precedential_status = c.precedential_status,
          opinion_id = o.id,
          plain_text = o.plain_text
        )
      }
    }

}
