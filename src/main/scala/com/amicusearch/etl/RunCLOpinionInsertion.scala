package com.amicusearch.etl

import com.amicusearch.etl.datatypes.courtlistener.opensearch.ConformedOpinion
import com.amicusearch.etl.opensearch.ConformOpinions
import com.amicusearch.etl.read.courtlistener.ReadProcessedOpinions
import com.amicusearch.etl.utils.WriterOpensearch
import com.typesafe.config.Config
import org.apache.spark.SparkContext
import org.apache.spark.sql.{DataFrame, SQLContext, SparkSession}

object RunCLOpinionInsertion {

  implicit val spark: SparkSession = SparkSession.builder.getOrCreate()
  implicit val sc: SparkContext = spark.sparkContext
  implicit val sql: SQLContext = spark.sqlContext

  def apply(appParams: AppParams, config: Config): Unit = {
    val writer = WriterOpensearch[ConformedOpinion](
      appParams.env,
      config.getString("opensearch.url"),
      config.getString("opensearch.username"),
      System.getenv("AMICUSEARCH_OPENSEARCH_PASSWORD"),
      "opinions"
    )(spark, sql)

    insertion(config.getString("courtlistener.results.local"), appParams.env, writer)
  }

  def insertion(path: String, env: AppParams.Environment.Value, writer: WriterOpensearch[ConformedOpinion]): Unit =
    (ReadProcessedOpinions(path, env) andThen
      ((df: DataFrame) => env match {
        // if we are testing, we don't want to embed the whole testing dataset
        case AppParams.Environment.local => df.limit(1)
        case AppParams.Environment.cci => df.limit(1)
        case AppParams.Environment.dev => df.limit(100)
        case _ => df
      }) andThen
      ConformOpinions() andThen
      writer.write)()
}
