package com.amicusearch.etl.datatypes.courtlistener.citations

case class ParsedCitation(id: String,
                          volume: String,
                          reporter: String,
                          page: String,
                          cite_type: String,
                          cluster_id: String)
