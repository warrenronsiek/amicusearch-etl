package com.amicusearch.etl

import com.amicusearch.etl.RunCLOpinionInsertion.{spark, sql}
import com.amicusearch.etl.datatypes.courtlistener.opensearch.ConformedEmbedding
import com.amicusearch.etl.opensearch.ConformEmbeddings
import com.amicusearch.etl.process.courtlistener.embed.Embed
import com.amicusearch.etl.read.courtlistener.ReadProcessedOpinions
import com.amicusearch.etl.utils.WriterOpensearch
import com.typesafe.config.Config
import org.apache.spark.SparkContext
import org.apache.spark.sql.{DataFrame, SQLContext, SparkSession}

object RunCLOpinionEmbedding {

  implicit val spark: SparkSession = SparkSession.builder.getOrCreate()
  implicit val sc: SparkContext = spark.sparkContext
  implicit val sql: SQLContext = spark.sqlContext

  def apply(appParams: AppParams, config: Config): Unit = {
    // have to pass env vars here because they are available in driver but not in executors.
    val writer = WriterOpensearch[ConformedEmbedding](
      appParams.env,
      config.getString("opensearch.url"),
      config.getString("opensearch.username"),
      System.getenv("AMICUSEARCH_OPENSEARCH_PASSWORD"),
      "embeddings", Some(10000))(spark, sql)

    insertion(config.getString("courtlistener.results.local"), appParams.env, writer)
  }

  def insertion(path: String, env: AppParams.Environment.Value, writer: WriterOpensearch[ConformedEmbedding]): Unit =
    (ReadProcessedOpinions(path, env) andThen
      ((df: DataFrame) => env match {
        // if we are testing, we don't want to embed the whole testing dataset
        case AppParams.Environment.local => df.limit(1)
        case  AppParams.Environment.cci => df.limit(1)
        case _ => df
      }) andThen
      Embed(System.getenv("COHERE_API_KEY")) andThen
      ConformEmbeddings() andThen
      writer.write)()

}
