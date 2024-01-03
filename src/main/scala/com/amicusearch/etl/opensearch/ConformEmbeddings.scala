package com.amicusearch.etl.opensearch

import com.amicusearch.etl.datatypes.courtlistener.embed.EmbeddedText
import com.amicusearch.etl.datatypes.courtlistener.opensearch.ConformedEmbedding
import org.apache.spark.sql.{DataFrame, Dataset}

object ConformEmbeddings {
  def apply(): Dataset[EmbeddedText] => Dataset[ConformedEmbedding] = {
    df =>
      import df.sparkSession.implicits._
      df.as[ConformedEmbedding]
  }
}
