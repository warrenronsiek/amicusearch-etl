package com.amicusearch.etl

import com.amicusearch.etl.datatypes.courtlistener.opinions.{OpinionsCleanWhitespace, OpinionsParsedHTML, OpinionsWithNulls}
import com.amicusearch.etl.process.courtlistener.opinions.{ParseHTML, ParseNulls, ParseWhitespace, RemoveTrivialOpinions}
import com.amicusearch.etl.read.ReadCourtsDB
import com.amicusearch.etl.read.courtlistener.{ReadCourtListenerCitations, ReadCourtListenerClusters, ReadCourtListenerCourts, ReadCourtListenerDockets, ReadCourtListenerOpinions}
import com.warren_r.sparkutils.snapshot.SnapshotTest
import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{DataFrame, Dataset, SQLContext, SparkSession}
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
  val courtListenerOpinions: Unit => DataFrame =
    ReadCourtListenerOpinions(getResourcePath("courtlistener_opinions_sample.jsonl"), casetextPartitionParams.env)(sparkSession) andThen(_.cache())
  val courtListenerCitations: Unit => DataFrame =
    ReadCourtListenerCitations(getResourcePath("courtlistener_citations_sample.jsonl"), casetextPartitionParams.env)(sparkSession) andThen(_.cache())
  val courtListenerCourts: Unit => DataFrame =
    ReadCourtListenerCourts(getResourcePath("courtlistener_courts_sample.jsonl"), casetextPartitionParams.env)(sparkSession) andThen(_.cache())
  val courtListenerDockets: Unit => DataFrame =
    ReadCourtListenerDockets(getResourcePath("courtlistener_dockets_sample.jsonl"), casetextPartitionParams.env)(sparkSession) andThen(_.cache())
  val courtListenerClusters: Unit => DataFrame =
    ReadCourtListenerClusters(getResourcePath("courtlistener_opinion_cluster_sample.jsonl"), casetextPartitionParams.env)(sparkSession) andThen(_.cache())

  val opinionProcessedNulls: Unit => Dataset[OpinionsWithNulls] = courtListenerOpinions andThen ParseNulls() andThen(_.cache())
  val opinionParsedHtml: Unit => Dataset[OpinionsParsedHTML] = opinionProcessedNulls andThen ParseHTML() andThen(_.cache())
  val opinionCleanedWhitespace: Unit => Dataset[OpinionsCleanWhitespace] = opinionParsedHtml andThen ParseWhitespace() andThen(_.cache())
  val opinionRemovedTrivial: Unit => Dataset[OpinionsCleanWhitespace] = opinionCleanedWhitespace andThen RemoveTrivialOpinions() andThen(_.cache())

}
