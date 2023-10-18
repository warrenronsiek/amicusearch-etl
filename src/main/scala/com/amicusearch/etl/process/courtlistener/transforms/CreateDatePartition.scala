package com.amicusearch.etl.process.courtlistener.transforms

import com.amicusearch.etl.datatypes.courtlistener.joins.OpinionCitation
import com.amicusearch.etl.datatypes.courtlistener.transforms.{OpinionDatePartition, OpinionLtree}
import org.apache.spark.sql.{Dataset, SQLContext, SparkSession}

object CreateDatePartition {

  def apply()(implicit spark: SparkSession, SQLContext: SQLContext): Dataset[OpinionLtree] => Dataset[OpinionDatePartition] = ds => {
    import SQLContext.implicits._
    ds.map(o => OpinionDatePartition(
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
      volume = o.volume,
      reporter = o.reporter,
      page = o.page,
      cite_type = o.cite_type,
      ltree = o.ltree,
      date_partition = o.date_filed.substring(0, 4)
    ))
  }

}
