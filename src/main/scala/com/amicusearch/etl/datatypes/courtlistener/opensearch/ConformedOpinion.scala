package com.amicusearch.etl.datatypes.courtlistener.opensearch

import com.amicusearch.etl.utils.serde.WriteableOpenSearch
import org.json4s._
import org.json4s.jackson.Serialization.write

case class ConformedOpinion(court_id: String,
                            court_citation_string: Option[String],
                            court_short_name: Option[String],
                            court_full_name: Option[String],
                            docket_id: String,
                            docket_number: Option[String],
                            docket_number_core: Option[String],
                            case_name_short: Option[String],
                            case_name: String,
                            case_name_full: Option[String],
                            slug: String,
                            region_partition: Option[String],
                            cluster_id: String,
                            date_filed: String,
                            scdb_id: Option[String],
                            headnotes: Option[String],
                            summary: Option[String],
                            citation_count: Option[String],
                            precedential_status: Option[String],
                            opinion_id: String,
                            plain_text: Option[String],
                            citations: Option[Array[String]],
                            ltree: Option[Array[String]],
                            generated_summary: Option[String],
                            outbound_citations: Array[String]) extends WriteableOpenSearch {
  override def id_str: String = opinion_id

  override def parent_id: Option[Long] = None

}
