package com.amicusearch.etl

import com.amicusearch.etl.datatypes.courtlistener.citations.ParsedCitation
import com.amicusearch.etl.datatypes.courtlistener.clusters.ClusterWithNulls
import com.amicusearch.etl.datatypes.courtlistener.courts.Court
import com.amicusearch.etl.datatypes.courtlistener.dockets.DocketsWithNulls
import com.amicusearch.etl.datatypes.courtlistener.joins.OpinionCitation
import com.amicusearch.etl.datatypes.courtlistener.opinions.OpinionsCleanWhitespace
import com.amicusearch.etl.datatypes.courtlistener.transforms.OpinionSummary
import com.amicusearch.etl.process.courtlistener.citations.ParseCitations
import com.amicusearch.etl.process.courtlistener.clusters.ClusterParseNulls
import com.amicusearch.etl.process.courtlistener.courts.{FilterCourts, ParseCourts}
import com.amicusearch.etl.process.courtlistener.dockets.ParseDockets
import com.amicusearch.etl.process.courtlistener.joins.{ClustersToOpinions, CourtsToDockets, DocketsToClusters, OpinionsToCitations}
import com.amicusearch.etl.process.courtlistener.opinions.{ParseHTML, ParseNulls, ParseWhitespace, RemoveTrivialOpinions}
import com.amicusearch.etl.process.courtlistener.transforms.{CreateCourtLtree, CreateSummary}
import com.amicusearch.etl.read.courtlistener._
import com.amicusearch.etl.utils.{ParquetWriter, USRegion}
import com.typesafe.config.Config
import org.apache.spark.SparkContext
import org.apache.spark.sql.{Dataset, SQLContext, SaveMode, SparkSession}

object RunCLOpinionProcessor {

  implicit val spark: SparkSession = SparkSession.builder.getOrCreate()
  implicit val sc: SparkContext = spark.sparkContext
  implicit val sql: SQLContext = spark.sqlContext

  def apply(appParams: AppParams, config: Config): Unit = {
    val writer: ParquetWriter = ParquetWriter(config.getString("courtlistener.results.local"), List("region_partition"))

    val courts: Dataset[Court] = processCourts(config.getString("courtlistener.courts"), appParams.env, appParams.states, appParams.includeFederal)()
    val dockets: Dataset[DocketsWithNulls] = processDockets(config.getString("courtlistener.dockets"), appParams.env)()
    val clusters: Dataset[ClusterWithNulls] = processClusters(config.getString("courtlistener.clusters"), appParams.env)()
    val opinions: Dataset[OpinionsCleanWhitespace] = processOpinions(config.getString("courtlistener.opinions"), appParams.env)()
    val citations: Dataset[ParsedCitation] = processCitations(config.getString("courtlistener.citations"), appParams.env)()

    (runJoins(courts, dockets, clusters, opinions, citations) andThen
      runTransforms(appParams.env, config.getString("mlserver.summarizer.url")) andThen
      writer.write).apply()
  }

  val processCourts: (String, AppParams.Environment.Value, List[USRegion.Value], Boolean) => Unit => Dataset[Court] =
    (path: String, env: AppParams.Environment.Value, states: List[USRegion.Value], includeFederal: Boolean) => _ => {
      (ReadCourtListenerCourts(path, env) andThen
        ParseCourts() andThen
        FilterCourts(states, includeFederal))(spark)
    }

  val processDockets: (String, AppParams.Environment.Value) => Unit => Dataset[DocketsWithNulls] =
    (path: String, env: AppParams.Environment.Value) => _ => {
      (ReadCourtListenerDockets(path, env) andThen
        ParseDockets())(spark)
    }

  val processClusters: (String, AppParams.Environment.Value) => Unit => Dataset[ClusterWithNulls] =
    (path: String, env: AppParams.Environment.Value) => _ => {
      (ReadCourtListenerClusters(path, env) andThen
        ClusterParseNulls())(spark)
    }

  val processOpinions: (String, AppParams.Environment.Value) => Unit => Dataset[OpinionsCleanWhitespace] =
    (path: String, env: AppParams.Environment.Value) => _ => {
      (ReadCourtListenerOpinions(path, env) andThen
        ParseNulls() andThen
        ParseHTML() andThen
        ParseWhitespace() andThen
        RemoveTrivialOpinions())(spark)
    }

  val processCitations: (String, AppParams.Environment.Value) => Unit => Dataset[ParsedCitation] =
    (path: String, env: AppParams.Environment.Value) => _ => {
      (ReadCourtListenerCitations(path, env) andThen
        ParseCitations())(spark).cache()
    }

  val runJoins: (
    Dataset[Court],
      Dataset[DocketsWithNulls],
      Dataset[ClusterWithNulls],
      Dataset[OpinionsCleanWhitespace],
      Dataset[ParsedCitation]) => Unit => Dataset[OpinionCitation] =
    (courts: Dataset[Court],
     dockets: Dataset[DocketsWithNulls],
     clusters: Dataset[ClusterWithNulls],
     opinions: Dataset[OpinionsCleanWhitespace],
     citations: Dataset[ParsedCitation]) => _ => {
      (CourtsToDockets(courts) andThen
        DocketsToClusters(clusters) andThen
        (df => df.cache()) andThen // opinions is a stream, so we want to cache results immediately prior to joining to the stream
        ClustersToOpinions(opinions) andThen
        OpinionsToCitations(citations))(dockets)
    }

  val runTransforms: (AppParams.Environment.Value, String) => Dataset[OpinionCitation] => Dataset[OpinionSummary] =
    (env: AppParams.Environment.Value, summarizerUrl: String) => (opinionCitations: Dataset[OpinionCitation]) => {
      (CreateCourtLtree() andThen
        CreateSummary(env, summarizerUrl))(opinionCitations)
    }
}
