package com.amicusearch.etl.utils

import com.amicusearch.etl.AppParams
import com.amicusearch.etl.utils.serde.WriteableOpenSearch
import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.sql.streaming.DataStreamWriter
import org.apache.spark.sql.{Dataset, SQLContext, SparkSession}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.ScalaObjectMapper
import requests.Response

import scala.util.{Failure, Success, Try}


class WriterOpensearch[T <: WriteableOpenSearch](env: AppParams.Environment.Value, url: String, user: String, password: String, indexName: String, timeOut: Option[Long] = None)
                                                (implicit spark: SparkSession, SQLContext: SQLContext)
  extends LazyLogging with java.io.Serializable {



  private def genericHttpOp(op: String, data: String, index: Option[String] = None): Unit = {
    val ix = index match {
      case Some(i) => i
      case None => indexName
    }
    op match {
      case "PUT" =>
        val resp: Response = requests.put(url + "/" + ix,
          verifySslCerts = env == AppParams.Environment.prod || env == AppParams.Environment.dev,
          auth = (user, password),
          headers = Map("Accept" -> "application/x-ndjson", "Content-Type" -> "application/x-ndjson"),
          data = data)
        logger.info(resp.text())
      case "POST" =>
        val resp: Response = requests.post(url + "/" + ix,
          verifySslCerts = env == AppParams.Environment.prod || env == AppParams.Environment.dev,
          auth = (user, password),
          headers = Map("Accept" -> "application/x-ndjson", "Content-Type" -> "application/x-ndjson"),
          data = data)
        logger.info(resp.text())
    }
  }

  private def initDB(): Unit = {
    Try {
      this.genericHttpOp("PUT", """{"mappings":{"properties":{"opinion_to_embedding":{"type":"join","relations":{"opinion":"embedding"}}}}}""")
    } match {
      case Success(_) => logger.info("Successfully created index " + indexName)
      case Failure(exception) if exception.getMessage contains ("already exists") => logger.info("Index " + indexName + " already exists")
      case Failure(exception) => throw exception
    }

  }

  initDB()

  val write: Dataset[T] => Unit = df => {

    val ws: DataStreamWriter[T] = df.writeStream.foreachBatch((df: Dataset[T], _: Long) => {

      df.foreachPartition((partition: Iterator[T]) => {
        val processed: Iterator[Int] = partition.grouped(100).map((rows: Seq[T]) => {
          val bulkPayload: String = rows.map((row: T) => {
            row.parent_id match {
              case Some(parentId) =>
                val metadataJson: String = f"""{"index":{"_index":"$indexName","_id":"${row.id_str}","routing":$parentId}}"""
                metadataJson + "\n" + row.toJSON + "\n"
              case None =>
                val metadataJson: String = f"""{"index":{"_index":"$indexName","_id":${row.id_str}}}"""
                metadataJson + "\n" + row.toJSON + "\n"
            }
          }).mkString("")
          val resp = requests.post(
            url + "/_bulk",
            verifySslCerts = env == AppParams.Environment.prod || env == AppParams.Environment.dev,
            headers = Map("Accept" -> "application/x-ndjson", "Content-Type" -> "application/x-ndjson"),
            data = bulkPayload,
            auth = (user, password))
          logger.info(resp.text())
          rows.length
        })
        logger.info("Wrote " + processed.sum + " rows to " + indexName)
      })
    })
    timeOut match {
      case Some(t) => ws.start().awaitTermination(t)
      case None => ws.start().awaitTermination()
    }
  }

}

object WriterOpensearch {
  def apply[T <: WriteableOpenSearch](env: AppParams.Environment.Value, url: String, user: String, password: String, indexName: String,
                                      timeOut: Option[Long] = None)(implicit spark: SparkSession, SQLContext: SQLContext): WriterOpensearch[T] =
    new WriterOpensearch[T](env, url, user, password, indexName, timeOut)

}
