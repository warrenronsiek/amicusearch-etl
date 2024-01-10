package com.amicusearch.etl

import com.amicusearch.etl.datatypes.courtlistener.opensearch.ConformedOpinion
import com.amicusearch.etl.opensearch.ConformOpinions
import com.amicusearch.etl.read.courtlistener.ReadProcessedOpinions
import com.amicusearch.etl.utils.{WriterOpensearch, WriterParquet}
import com.typesafe.config.Config
import org.apache.spark.SparkContext
import org.apache.spark.sql.{SQLContext, SparkSession}
import org.slf4j.LoggerFactory
import java.nio.file.{FileSystems, Files}
import scala.collection.JavaConverters._

object RunCLOpinionInsertion {

  implicit val spark: SparkSession = SparkSession.builder.getOrCreate()
  implicit val sc: SparkContext = spark.sparkContext
  implicit val sql: SQLContext = spark.sqlContext

  private val logger = LoggerFactory.getLogger("RunCLOpinionInsertion")

  def apply(appParams: AppParams, config: Config): Unit = {
//    val writer = WriterOpensearch[ConformedOpinion](
//      appParams.env,
//      config.getString("opensearch.url"),
//      config.getString("opensearch.username"),
//      System.getenv("AMICUSEARCH_OPENSEARCH_PASSWORD"),
//      "opinions",
//      Some(10000)
//    )(spark, sql)

//    insertion(config.getString("courtlistener.results.local"), appParams.env, writer)

    val writer = WriterParquet(
      config.getString("courtlistener.results.local_temp"),
      List("court_id")
    )(spark, sql)

    logger.info("instantiating read")
    val df = ReadProcessedOpinions(config.getString("courtlistener.results.local"), appParams.env).apply()
    logger.info(s"count: ${df.count()}")
  }

  def insertion(path: String, env: AppParams.Environment.Value, writer: WriterOpensearch[ConformedOpinion]): Unit =
    (ReadProcessedOpinions(path, env) andThen
      ConformOpinions() andThen
      writer.write)()
}
