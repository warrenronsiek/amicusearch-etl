package com.amicusearch.etl.datatypes.courtlistener

case class ClusterWithNulls(id: String,
                            date_filed: String,
                            scdb_id: Option[String],
                            headnotes: Option[String],
                            summary: Option[String],
                            citation_count: Option[String],
                            precedential_status: Option[String],
                            docket_id: Option[String])
