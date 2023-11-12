package com.amicusearch.etl.utils

import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._
import util.retry.blocking.{Failure, Retry, RetryStrategy, Success}
import upickle.default._
import upickle.default.{macroRW, ReadWriter => RW}
import util.retry.blocking.RetryStrategy.RetryStrategyProducer

// curl --request POST \
//     --url https://api.cohere.ai/v1/embed \
//     --header 'accept: application/json' \
//     --header 'authorization: Bearer fMEIGM2Gn5dysiiztSIqfqkhJDxKde2V8DSDJlvC' \
//     --header 'content-type: application/json' \
//     --data '
//{
//  "texts": [
//    "hello",
//    "goodbye"
//  ],
//  "model": "embed-english-v3.0"
//  "truncate": "END"
//}
//'

object CohereEmbedder extends LazyLogging with java.io.Serializable {

  private case class EmbeddingResponse(id: String, texts: Array[String], embeddings: Array[Array[Double]]) extends java.io.Serializable

  private object EmbeddingResponse {
    implicit val rw: RW[EmbeddingResponse] = macroRW
  }

  private case class EmbeddingPayload(model: String,
                                      input_type: String,
                                      texts: Seq[String],
                                      truncate: String) extends java.io.Serializable

  private object EmbeddingPayload {
    implicit val rw: RW[EmbeddingPayload] = macroRW
  }

  implicit val retryStrategy: RetryStrategyProducer = RetryStrategy.fibonacciBackOff(1.seconds, maxAttempts = 11)

  def embed(s: String): Array[Double] = {
    Retry(
      requests.post("https://api.cohere.ai/v1/embed",
        data = write(EmbeddingPayload("embed-english-v3.0", "search_document", Array(s), "END")),
        headers = Map("Authorization" -> s"Bearer ${System.getenv("COHERE_API_KEY")}", "Content-Type" -> "application/json"))
    ) match {
      case Success(r) => read[EmbeddingResponse](r.text).embeddings(0)
      case Failure(e) =>
        logger.error(s"Failed to embed string with error: ${e.getMessage}")
        throw e
    }
  }

  def embed(s: Seq[String]): Array[(String, Array[Double])] = {
    Retry(
      requests.post("https://api.cohere.ai/v1/embed",
        data = write(EmbeddingPayload("embed-english-v3.0", "search_document", s, "END")),
        headers = Map("Authorization" -> s"Bearer ${System.getenv("COHERE_API_KEY")}", "Content-Type" -> "application/json"))
    ) match {
      case Success(r) => read[EmbeddingResponse](r.text).texts zip read[EmbeddingResponse](r.text).embeddings
      case Failure(e) =>
        logger.error(s"Failed to embed string with error: ${e.getMessage}")
        throw e
    }
  }

}
