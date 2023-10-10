package com.amicusearch.etl.process.courtlistener.courts

import com.amicusearch.etl.datatypes.courtlistener.Court
import org.apache.spark.sql.{Dataset, SparkSession}

object FilterCourts {

  def nameContainsState(names: List[String], states: List[String]): Boolean = {
    states.map(state => names.map(name => name.contains(state)).reduce(_ || _)).reduce(_ || _)
  }

  def apply(states: List[String] = List[String](), includeFederal: Boolean = false)(implicit spark: SparkSession): Dataset[Court] => Dataset[Court] = ds => {
    import spark.implicits._
    if (states.isEmpty && !includeFederal) {
      ds
    } else if (states.isEmpty && includeFederal) {
      ds.filter(_.jurisdiction.startsWith("F"))
    } else {
      ds.filter((c: Court) => {
        val nameValues = List(c.full_name, c.short_name).flatten // flatten removes nulls
        val isIncludedFederalCourt: Boolean = if (includeFederal) c.jurisdiction.startsWith("F") else false
        val nameContainsIncludedState: Boolean = nameContainsState(nameValues, states)
        isIncludedFederalCourt || nameContainsIncludedState
      })
    }
  }
}
