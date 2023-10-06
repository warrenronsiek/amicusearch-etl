package com.amicusearch.etl.metadata.courtsdb


import com.amicusearch.etl.datatypes.{CasetextCase, CourtsDBCourt}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SQLContext, SparkSession}
import scala.collection.mutable


// Removes periods from the jurisdiction column in the dataframe
object RegularizeJurisdiction {
  def apply()(implicit spark: SparkSession, SQLContext: SQLContext): Dataset[Row] => Dataset[CourtsDBCourt] = ds => {
    import SQLContext.implicits._
    ds.map((r:Row) => CourtsDBCourt(
      case_types = r.getAs[mutable.Seq[String]]("case_types"),
      citation_string = r.getAs[String]("citation_string"),
      court_url = r.getAs[String]("court_url"),
      dates = r.getAs[mutable.Seq[String]]("dates"),
      examples = r.getAs[mutable.Seq[String]]("examples"),
      id = r.getAs[String]("id"),
      jurisdiction = r.getAs[String]("jurisdiction") match {
        case null => null
        case s:String => s.replace(".", "").toLowerCase()
      },
      level = r.getAs[String]("level"),
      location = r.getAs[String]("location"),
      locations = r.getAs[Int]("locations"),
      name = r.getAs[String]("name"),
      name_abbreviation = r.getAs[String]("name_abbreviation"),
      regex = r.getAs[mutable.Seq[String]]("regex"),
      system = r.getAs[String]("system"),
      court_type = r.getAs[String]("court_type")
    ))
  }
}
