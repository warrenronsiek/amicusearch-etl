package com.amicusearch.etl

import com.amicusearch.etl.read.ReadCourtsDB
import com.warren_r.sparkutils.snapshot.SnapshotTest
import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{DataFrame, SQLContext, SparkSession}
import org.apache.spark.sql.functions._

trait GenericAmicusearchTest extends SnapshotTest with LazyLogging{
  val sparkConf: SparkConf = new SparkConf()
    .set("appName", "amicusearch-etl")
    .set("spark.sql.files.maxRecordsPerFile", "20000000")
    .set("spark.sql.parquet.binaryAsString", "true")
    .set("spark.sql.session.timeZone", "UTC")
  implicit val sparkSession: SparkSession = SparkSession.builder
    .config(sparkConf).master("local[*]").getOrCreate()
  implicit val sc: SparkContext = sparkSession.sparkContext
  implicit val sql: SQLContext = sparkSession.sqlContext

  def getResourcePath(resourceName:String) = getClass.getResource("/" + resourceName).getPath

  import sql.implicits._

  val casetextPartitionParams: AppParams = new AppParams(AppParams.Mode.partitionCasetext, AppParams.Environment.local)

  val courtsDB: Unit => DataFrame = ReadCourtsDB("src/test/resources/courts_db_sample.json")
}
