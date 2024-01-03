package com.amicusearch.etl.datatypes.courtlistener.opensearch

import com.amicusearch.etl.utils.serde.WriteableOpenSearch
import org.json4s._

case class ConformedEmbedding(date_filed: String,
                              citation_count: String,
                              precedential_status: String,
                              opinion_id: String,
                              ltree: String,
                              text: String,
                              text_id: String,
                              embedding: Array[Double]) extends WriteableOpenSearch {

  def id_str: String = this.text_id

  def parent_id: Option[Long] = Some(this.opinion_id.toLong)


}
