package com.amicusearch.etl.process.courtlistener.courts

import com.amicusearch.etl.datatypes.courtlistener.courts.Court
import com.amicusearch.etl.utils.USRegion
import org.apache.spark.sql.{Dataset, SparkSession}
import org.slf4j.LoggerFactory

object FilterCourts {

  def nameContainsRegion(names: List[String], regions: List[USRegion.Value]): Boolean = {
    regions.map(region => names.map(name => name.toLowerCase.replace(" ", "_").contains(region.toString)).reduce(_ || _)).reduce(_ || _)
  }

  def apply(court_ids: List[String] = List[String](), states: List[USRegion.Value] = List[USRegion.Value](), includeFederal: Boolean = false)(implicit spark: SparkSession): Dataset[Court] => Dataset[Court] = ds => {
    val logger = LoggerFactory.getLogger("FilterCourts")
    if (court_ids.nonEmpty) {
      logger.info(s"Filtering courts by ids: ${court_ids.mkString(", ")}")
      ds.filter(c => court_ids.contains(c.id))
    } else if (states.isEmpty && !includeFederal) {
      logger.info("No courts filtered")
      ds
    } else if (states.isEmpty && includeFederal) {
      logger.info("Filtering federal courts")
      ds.filter(_.jurisdiction.startsWith("F"))
    } else {
      logger.info(s"Filtering courts by states: ${states.mkString(", ")}")
      ds.filter((c: Court) => {
        val nameValues = List(c.full_name, c.short_name).flatten // flatten removes nulls
        val isIncludedFederalCourt: Boolean = if (includeFederal) c.jurisdiction.startsWith("F") else false
        val nameContainsIncludedState: Boolean = nameContainsRegion(nameValues, states)
        isIncludedFederalCourt || nameContainsIncludedState
      })
    }
  }
}
