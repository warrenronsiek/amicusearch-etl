package com.amicusearch.etl.datatypes.courtlistener.opinions

case class OpinionsWithNulls(
                              id: String,
                              plain_text: Option[String],
                              html: Option[String],
                              html_anon_2020: Option[String],
                              xml_harvard: Option[String],
                              html_with_citations: Option[String],
                              cluster_id: Option[String],
                            )
