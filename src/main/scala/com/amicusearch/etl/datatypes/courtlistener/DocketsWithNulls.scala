package com.amicusearch.etl.datatypes.courtlistener

case class DocketsWithNulls(id: String,
                            case_name_short: Option[String],
                            case_name: String,
                            case_name_full: Option[String],
                            slug: String,
                            docket_number: Option[String],
                            docket_number_core: Option[String],
                            court_id: String)