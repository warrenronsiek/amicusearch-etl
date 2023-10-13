package com.amicusearch.etl

import com.amicusearch.etl.datatypes.courtlistener.citations.ParsedCitation
import com.amicusearch.etl.datatypes.courtlistener.clusters.ClusterWithNulls
import com.amicusearch.etl.datatypes.courtlistener.courts.Court
import com.amicusearch.etl.datatypes.courtlistener.dockets.DocketsWithNulls
import com.amicusearch.etl.datatypes.courtlistener.joins.{ClusterOpinion, CourtDocket, DocketCluster, OpinionCitation}
import com.amicusearch.etl.datatypes.courtlistener.opinions.{OpinionsCleanWhitespace, OpinionsParsedHTML, OpinionsWithNulls}
import com.amicusearch.etl.datatypes.courtlistener.transforms.{OpinionDatePartition, OpinionLtree}
import com.amicusearch.etl.process.courtlistener.citations.ParseCitations
import com.amicusearch.etl.process.courtlistener.clusters.ClusterParseNulls
import com.amicusearch.etl.process.courtlistener.courts.{FilterCourts, ParseCourts}
import com.amicusearch.etl.process.courtlistener.dockets.ParseDockets
import com.amicusearch.etl.process.courtlistener.joins.{ClustersToOpinions, CourtsToDockets, DocketsToClusters, OpinionsToCitations}
import com.amicusearch.etl.process.courtlistener.opinions.{ParseHTML, ParseNulls, ParseWhitespace, RemoveTrivialOpinions}
import com.amicusearch.etl.process.courtlistener.transforms.{CreateCourtLtree, CreateDatePartition}
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
  val processedCitations: Unit => Dataset[ParsedCitation] = courtListenerCitations andThen ParseCitations()

  val courts: Unit => Dataset[Court] = courtListenerCourts andThen ParseCourts() andThen FilterCourts()
  val clusters: Unit => Dataset[ClusterWithNulls] = courtListenerClusters andThen ClusterParseNulls()
  val parseDockets: Unit => Dataset[DocketsWithNulls] = courtListenerDockets andThen ParseDockets()
  val courtsToDockets: Dataset[DocketsWithNulls] => Dataset[CourtDocket] = CourtsToDockets(courts())
  val courtsJoinedDockets: Dataset[CourtDocket] = courtsToDockets(parseDockets()).cache()
  val docketsToCluster: Dataset[CourtDocket] => Dataset[DocketCluster] = DocketsToClusters(clusters())
  val docketsJoinedClusters: Dataset[DocketCluster] = docketsToCluster(courtsJoinedDockets).cache()
  val opinionsToClusters: Dataset[DocketCluster] => Dataset[ClusterOpinion] = ClustersToOpinions(opinionRemovedTrivial())
  val opinionsJoinedClusters: Dataset[ClusterOpinion] = opinionsToClusters(docketsJoinedClusters).cache()
  val opinionsToCitations: Dataset[ClusterOpinion] => Dataset[OpinionCitation] = OpinionsToCitations(processedCitations())

  val createCourtLtree: Dataset[ClusterOpinion] => Dataset[OpinionLtree] = opinionsToCitations andThen CreateCourtLtree()
  val courtLtree: Dataset[OpinionLtree] = createCourtLtree(opinionsJoinedClusters).cache()
  val datePartitions: Dataset[OpinionDatePartition] = CreateDatePartition().apply(courtLtree).cache()
}
