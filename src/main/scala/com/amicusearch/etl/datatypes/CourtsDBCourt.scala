package com.amicusearch.etl.datatypes
import scala.collection.mutable
case class CourtsDBCourt(case_types: mutable.Seq[String],
                         citation_string: String,
                         court_url: String,
                         dates: mutable.Seq[String],
                         examples: mutable.Seq[String],
                         id: String,
                         jurisdiction: String,
                         level: String,
                         location: String,
                         locations: Int,
                         name: String,
                         name_abbreviation: String,
                         regex: mutable.Seq[String],
                         system: String,
                         court_type: String)
