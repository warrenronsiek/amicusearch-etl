package com.amicusearch.etl.process.courtlistener.transforms

import com.amicusearch.etl.AppParams
import com.amicusearch.etl.datatypes.courtlistener.transforms.{OpinionLtree, OpinionSummary}
import org.apache.spark.sql.{Dataset, SQLContext, SparkSession}

object Deduplicate {
  def apply()(implicit spark: SparkSession, SQLContext: SQLContext): Dataset[OpinionLtree] => Dataset[OpinionLtree] = opinions => {
    import SQLContext.implicits._
    opinions.toDF().dropDuplicates("opinion_id").as[OpinionLtree]
  }
}
