package com.amicusearch.etl.datatypes.courtlistener.opinions

case class OpinionsCleanWhitespace(id: String,
                                   plain_text: Option[String],
                                   cluster_id: Option[String])
