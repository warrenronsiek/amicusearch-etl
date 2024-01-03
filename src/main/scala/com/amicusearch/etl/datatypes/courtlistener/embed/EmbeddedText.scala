package com.amicusearch.etl.datatypes.courtlistener.embed

case class EmbeddedText(date_filed: String,
                        citation_count: String,
                        precedential_status: String,
                        opinion_id: String,
                        ltree: String,
                        text: String,
                        text_id: String,
                        embedding: Array[Double])
