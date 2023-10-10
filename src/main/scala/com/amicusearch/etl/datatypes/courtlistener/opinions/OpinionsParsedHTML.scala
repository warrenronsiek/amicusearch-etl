package com.amicusearch.etl.datatypes.courtlistener.opinions

case class OpinionsParsedHTML(id: String,
                              plain_text: Option[String],
                              cluster_id: Option[String])
