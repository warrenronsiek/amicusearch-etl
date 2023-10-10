package com.amicusearch.etl.datatypes.courtlistener.courts

case class Court(id: String,
                 citation_string: Option[String],
                 short_name: Option[String],
                 full_name: Option[String],
                 jurisdiction: String)
