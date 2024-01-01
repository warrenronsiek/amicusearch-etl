package com.amicusearch.etl.process.courtlistener.courts

import com.amicusearch.etl.datatypes.courtlistener.courts.Court
import com.amicusearch.etl.utils.USRegion
import org.apache.spark.api.java.Optional
import org.apache.spark.sql.{Dataset, SparkSession}

object FilterCourts {

  def nameContainsRegion(names: List[String], regions: List[USRegion.Value]): Boolean = {
    regions.map(region => names.map(name => name.toLowerCase.replace(" ", "_").contains(region.toString)).reduce(_ || _)).reduce(_ || _)
  }

  def apply(court_ids: List[String] = List[String](), states: List[USRegion.Value] = List[USRegion.Value](), includeFederal: Boolean = false)(implicit spark: SparkSession): Dataset[Court] => Dataset[Court] = ds => {
    import spark.implicits._
    if (court_ids.nonEmpty) {
      ds.filter(c => court_ids.contains(c.id))
    } else if (states.isEmpty && !includeFederal) {
      ds
    } else if (states.isEmpty && includeFederal) {
      ds.filter(_.jurisdiction.startsWith("F"))
    } else {
      ds.filter((c: Court) => {
        val nameValues = List(c.full_name, c.short_name).flatten // flatten removes nulls
        val isIncludedFederalCourt: Boolean = if (includeFederal) c.jurisdiction.startsWith("F") else false
        val nameContainsIncludedState: Boolean = nameContainsRegion(nameValues, states)
        isIncludedFederalCourt || nameContainsIncludedState
      })
    }
  }
}
