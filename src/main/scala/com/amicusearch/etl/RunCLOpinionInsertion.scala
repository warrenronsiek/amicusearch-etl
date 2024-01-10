package com.amicusearch.etl

import com.amicusearch.etl.datatypes.courtlistener.opensearch.ConformedOpinion
import com.amicusearch.etl.opensearch.ConformOpinions
import com.amicusearch.etl.read.courtlistener.ReadProcessedOpinions
import com.amicusearch.etl.utils.WriterOpensearch
import com.typesafe.config.Config
import org.apache.spark.SparkContext
import org.apache.spark.sql.{SQLContext, SparkSession}

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
      ConformOpinions() andThen
      writer.write)()
}
