package com.amicusearch.etl.opensearch

import com.amicusearch.etl.datatypes.courtlistener.opensearch.ConformedOpinion
import org.apache.spark.sql.{DataFrame, Dataset}

object ConformOpinions {
  def apply(): DataFrame => Dataset[ConformedOpinion] = {
    df =>
      import df.sparkSession.implicits._
      df.as[ConformedOpinion]
  }

}
