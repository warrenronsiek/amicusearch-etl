package com.amicusearch.etl.datatypes.courtlistener.opensearch

import com.amicusearch.etl.utils.serde.WriteableOpenSearch

case class ConformedEmbedding(date_filed: String,
                              citation_count: String,
                              precedential_status: String,
                              opinion_id: String,
                              ltree: Array[String],
                              text: String,
                              text_id: String,
                              embedding: Array[Double]) extends WriteableOpenSearch {

  def id_str: String = this.text_id

  def parent_id: Option[Long] = Some(this.opinion_id.toLong)

  override def ignoreFields: Option[Set[String]] = Some(Set("opinion_id", "text_id"))


}
