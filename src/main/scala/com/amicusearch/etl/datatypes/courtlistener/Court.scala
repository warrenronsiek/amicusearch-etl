package com.amicusearch.etl.datatypes.courtlistener


case class Court(id: String,
                 pacer_court_id: Option[String],
                 pacer_has_rss_feed: Option[String],
                 pacer_rss_entry_types: Option[String],
                 fjc_court_id: Option[String],
                 date_modified: Option[String],
                 in_use: Option[String],
                 has_opinion_scraper: Option[String],
                 has_oral_argument_scraper: Option[String],
                 position: Option[String],
                 citation_string: Option[String],
                 short_name: Option[String],
                 full_name: Option[String],
                 url: Option[String],
                 start_date: Option[String],
                 end_date: Option[String],
                 jurisdiction: String,
                 notes: Option[String])
