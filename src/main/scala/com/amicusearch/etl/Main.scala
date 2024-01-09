package com.amicusearch.etl

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.SparkContext
import org.apache.spark.sql.{SQLContext, SparkSession}
import org.slf4j.LoggerFactory

object Main {
  def run(params: AppParams): Unit = {
    val logger = LoggerFactory.getLogger("Main")
    val conf: Config = ConfigFactory.load(params.env match {
      case AppParams.Environment.prod => "prod.conf"
      case AppParams.Environment.dev => "dev.conf"
      case AppParams.Environment.local => "local.conf"
      case AppParams.Environment.cci => "cci.conf"
    })
    logger.info(s"Running in ${params.env} mode")
    implicit val spark: SparkSession = SparkSession.builder
      .config(SparkConf.sparkConf(conf.getString("courtlistener.results.checkpoint"))).getOrCreate()
    implicit val sc: SparkContext = spark.sparkContext
    implicit val sql: SQLContext = spark.sqlContext

    params.mode match {
      case AppParams.Mode.CLOpinionProcessor =>
        logger.info("Running CLOpinionProcessor")
        RunCLOpinionProcessor(params, conf)
      case AppParams.Mode.CLOpinionInsert =>
        logger.info("Running CLOpinionInsert")
        RunCLOpinionInsertion(params, conf)
      case AppParams.Mode.CLOpinionEmbed =>
        logger.info("Running CLOpinionEmbed")
        RunCLOpinionEmbedding(params, conf)
      case _ => logger.error("Invalid mode")
    }
  }

  def main(args: Array[String]): Unit = {
    run(AppParams(args))
  }
}
