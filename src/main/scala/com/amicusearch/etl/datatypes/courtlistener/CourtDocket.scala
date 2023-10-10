package com.amicusearch.etl.datatypes.courtlistener

case class CourtDocket(court_id: String,
                       court_citation_string: Option[String],
                       court_short_name: Option[String],
                       court_full_name: Option[String],
                       docket_id: String,
                       docket_number: Option[String],
                       docket_number_core: Option[String],
                       case_name_short: Option[String],
                       case_name: String,
                       case_name_full: Option[String],
                       slug: String)
