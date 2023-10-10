package com.amicusearch.etl.process.courtlistener.clusters

import com.amicusearch.etl.datatypes.courtlistener.{ClusterWithNulls, OpinionsWithNulls}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SQLContext, SparkSession}
import com.amicusearch.etl.utils.Funcs

object ClusterParseNulls {

  def apply()(implicit spark: SparkSession, SQLContext: SQLContext): Dataset[Row] => Dataset[ClusterWithNulls] = ds => {
    import SQLContext.implicits._
    ds.map((r: Row) => {
      ClusterWithNulls(
        id = r.getAs[String]("id"),
        date_created = r.getAs[String]("date_created"),
        date_modified = r.getAs[String]("date_modified"),
        judges = Funcs.parseNan(r.getAs[String]("judges")),
        date_filed = r.getAs[String]("date_filed"),
        date_filed_is_approximate = r.getAs[String]("date_filed_is_approximate"),
        slug = r.getAs[String]("slug"),
        case_name_short = r.getAs[String]("case_name_short"),
        case_name = r.getAs[String]("case_name"),
        case_name_full = r.getAs[String]("case_name_full"),
        scdb_id = Funcs.parseNan(r.getAs[String]("scdb_id")),
        scdb_votes_majority = Funcs.parseNan(r.getAs[String]("scdb_votes_majority")),
        scdb_votes_minority = Funcs.parseNan(r.getAs[String]("scdb_votes_minority")),
        source = Funcs.parseNan(r.getAs[String]("source")),
        procedural_history = Funcs.parseNan(r.getAs[String]("procedural_history")),
        attorneys = Funcs.parseNan(r.getAs[String]("attorneys")),
        nature_of_suit = Funcs.parseNan(r.getAs[String]("nature_of_suit")),
        posture = Funcs.parseNan(r.getAs[String]("posture")),
        syllabus = Funcs.parseNan(r.getAs[String]("syllabus")),
        headnotes = Funcs.parseNan(r.getAs[String]("headnotes")),
        summary = Funcs.parseNan(r.getAs[String]("summary")),
        disposition = Funcs.parseNan(r.getAs[String]("disposition")),
        history = Funcs.parseNan(r.getAs[String]("history")),
        other_dates = Funcs.parseNan(r.getAs[String]("other_dates")),
        cross_reference = Funcs.parseNan(r.getAs[String]("cross_reference")),
        correction = Funcs.parseNan(r.getAs[String]("correction")),
        citation_count = Funcs.parseNan(r.getAs[String]("citation_count")),
        precedential_status = Funcs.parseNan(r.getAs[String]("precedential_status")),
        date_blocked = Funcs.parseNan(r.getAs[String]("date_blocked")),
        blocked = Funcs.parseNan(r.getAs[String]("blocked")),
        filepath_json_harvard = Funcs.parseNan(r.getAs[String]("filepath_json_harvard")),
        docket_id = Funcs.parseNan(r.getAs[String]("docket_id")),
        arguments = Funcs.parseNan(r.getAs[String]("arguments")),
        headmatter = Funcs.parseNan(r.getAs[String]("headmatter"))
      )
    })
  }
}
