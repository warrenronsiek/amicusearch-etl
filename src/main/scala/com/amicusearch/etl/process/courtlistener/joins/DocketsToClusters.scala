package com.amicusearch.etl.process.courtlistener.joins

import com.amicusearch.etl.datatypes.courtlistener.{ClusterWithNulls, CourtDocket, DocketCluster}
import org.apache.spark.sql.{Dataset, SQLContext, SparkSession}

object DocketsToClusters {
  def apply(courtDockets: Dataset[CourtDocket])(implicit spark: SparkSession, SQLContext: SQLContext): Dataset[ClusterWithNulls] => Dataset[DocketCluster] =
    cluster => {
      import SQLContext.implicits._
      cluster.joinWith(courtDockets, cluster("docket_id") === courtDockets("docket_id"), "inner").map {
        case (c: ClusterWithNulls, d: CourtDocket) => DocketCluster(
          court_id = d.court_id,
          court_citation_string = d.court_citation_string,
          court_short_name = d.court_short_name,
          court_full_name = d.court_full_name,
          docket_id = d.docket_id,
          docket_number = d.docket_number,
          docket_number_core = d.docket_number_core,
          case_name_short = d.case_name_short,
          case_name = d.case_name,
          case_name_full = d.case_name_full,
          slug = d.slug,
          date_filed = c.date_filed,
          scdb_id = c.scdb_id,
          headnotes = c.headnotes,
          summary = c.summary,
          citation_count = c.citation_count,
          precedential_status = c.precedential_status,
          cluster_id = c.id
        )
      }
    }
}
