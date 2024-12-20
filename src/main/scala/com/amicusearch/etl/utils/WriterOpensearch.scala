package com.amicusearch.etl.utils

import com.amicusearch.etl.AppParams
import com.amicusearch.etl.utils.serde.WriteableOpenSearch
import org.apache.spark.sql.streaming.DataStreamWriter
import org.apache.spark.sql.{Dataset, SQLContext, SparkSession}
import org.slf4j.LoggerFactory
import requests.Response
import upickle.default._
import upickle.default.{macroRW, ReadWriter => RW}
import scala.util.{Failure, Success, Try}

class WriterOpensearch[T <: WriteableOpenSearch](env: AppParams.Environment.Value, url: String, user: String, password: String, indexName: String, timeOut: Option[Long] = None)
                                                (implicit spark: SparkSession, SQLContext: SQLContext)
  extends java.io.Serializable {

  private val logger = LoggerFactory.getLogger("WriterOpensearch")

  case class Shards(total: Int, successful: Int, failed: Int)

  implicit object Shards {
    implicit val rw: RW[Shards] = macroRW
  }

  case class Index(_index: String, _id: String, result: String, _shards: Shards, _seq_no: Int, _primary_term: Int, status: Int)

  implicit object Index {
    implicit val rw: RW[Index] = macroRW
  }

  case class IndexContainer(index: Index)

  implicit object IndexContainer {
    implicit val rw: RW[IndexContainer] = macroRW
  }

  case class ResponsePayload(took: Int, errors: Boolean, items: Array[IndexContainer])

  implicit object ResponsePayload {
    implicit val rw: RW[ResponsePayload] = macroRW
  }

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
      this.genericHttpOp("PUT", """{
                                  |  "mappings": {
                                  |    "properties": {
                                  |      "embedding": {
                                  |        "type": "knn_vector",
                                  |        "dimension": 1024
                                  |      },
                                  |      "opinion_to_embedding": {
                                  |        "type": "join",
                                  |        "relations": {
                                  |          "opinion": "embedding"
                                  |        }
                                  |      },
                                  |      "court_citation_string": {
                                  |        "type": "keyword"
                                  |      },
                                  |      "case_name": {
                                  |        "type": "text",
                                  |        "analyzer": "english"
                                  |      },
                                  |      "case_name_short": {
                                  |        "type": "text",
                                  |        "analyzer": "english"
                                  |      },
                                  |      "citations": {
                                  |        "type": "text",
                                  |        "analyzer": "english"
                                  |      },
                                  |      "citation_count": {
                                  |        "type": "integer"
                                  |      },
                                  |      "court_full_name": {
                                  |        "type": "keyword"
                                  |      },
                                  |      "court_id": {
                                  |        "type": "keyword"
                                  |      },
                                  |      "court_short_name": {
                                  |        "type": "keyword"
                                  |      },
                                  |      "date_filed": {
                                  |        "type": "date"
                                  |      },
                                  |      "docket_id": {
                                  |        "type": "text"
                                  |      },
                                  |      "docket_number": {
                                  |        "type": "text"
                                  |      },
                                  |      "docket_number_core": {
                                  |        "type": "text"
                                  |      },
                                  |      "generated_summary": {
                                  |        "type": "text",
                                  |        "analyzer": "english"
                                  |      },
                                  |      "ltree": {
                                  |        "type": "keyword"
                                  |      },
                                  |      "outbound_citations": {
                                  |        "type": "text",
                                  |        "analyzer": "english"
                                  |      },
                                  |      "plain_text": {
                                  |        "type": "text",
                                  |        "analyzer": "english"
                                  |      },
                                  |        "precedential_status": {
                                  |            "type": "keyword"
                                  |        },
                                  |      "slug": {
                                  |        "type": "keyword"
                                  |      },
                                  |      "text": {
                                  |        "type": "text",
                                  |        "analyzer": "english"
                                  |      }
                                  |    }
                                  |  }
                                  |}""".stripMargin)
    } match {
      case Success(_) => logger.info("Successfully created index " + indexName)
      case Failure(exception) if exception.getMessage contains "already exists" => logger.info("Index " + indexName + " already exists")
      case Failure(exception) =>
        logger.error("Failed to create index " + indexName + " with error: " + exception.getMessage)
        throw exception
    }
  }

  initDB()

  val write: Dataset[T] => Unit = df => {
    val ws: DataStreamWriter[T] = df.writeStream.foreachBatch((df: Dataset[T], _: Long) => {
      logger.info(s"Beginning write to $indexName")
      df.foreachPartition((partition: Iterator[T]) => {
        val processed: Iterator[Int] = partition.grouped(10).map((rows: Seq[T]) => {
          logger.info("Writing batch of " + rows.length + " rows to " + indexName)
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
          logger.info("Generated bulk payload of size " + bulkPayload.length)
          val resp = requests.post(
            url + "/_bulk",
            verifySslCerts = env == AppParams.Environment.prod || env == AppParams.Environment.dev,
            headers = Map("Accept" -> "application/x-ndjson", "Content-Type" -> "application/x-ndjson"),
            data = bulkPayload,
            auth = (user, password))
          logger.info(s"got a response of: ${resp.statusCode}")
          val response = read[ResponsePayload](resp.text())
          if (response.errors) {
            logger.error("Failed to write batch with error: " + resp.text())
          }
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
