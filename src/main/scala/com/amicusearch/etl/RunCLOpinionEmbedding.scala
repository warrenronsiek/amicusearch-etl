package com.amicusearch.etl

import com.amicusearch.etl.process.courtlistener.embed.Embed
import com.amicusearch.etl.read.courtlistener.ReadProcessedOpinions
import com.amicusearch.etl.utils.WriterPGSQL
import com.typesafe.config.Config
import org.apache.spark.SparkContext
import org.apache.spark.sql.{SQLContext, SparkSession}

object RunCLOpinionEmbedding {

  implicit val spark: SparkSession = SparkSession.builder.getOrCreate()
  implicit val sc: SparkContext = spark.sparkContext
  implicit val sql: SQLContext = spark.sqlContext

  def apply(appParams: AppParams, config: Config): Unit = {
    // have to pass env vars here because they are available in driver but not in executors.
    val writer = WriterPGSQL(config.getString("pgsql.url"),
      config.getString("pgsql.username"),
      System.getenv("AMICUSEARCH_PG_PASSWORD"),
      config.getString("inserts.embeddings.tablename"))

    (ReadProcessedOpinions(config.getString("courtlistener.results.local"), appParams.env) andThen
      Embed(System.getenv("COHERE_API_KEY")) andThen
      writer.write)()
  }

}
