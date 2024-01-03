package com.amicusearch.etl.utils

import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._
import util.retry.blocking.{Failure, Retry, RetryStrategy, Success}
import util.retry.blocking.RetryStrategy.RetryStrategyProducer
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization.write
import org.json4s._

class CohereEmbedder(cohereKey: String) extends LazyLogging with java.io.Serializable {
  assert(cohereKey != null, "Cohere API key not found in environment variables")
  assert(cohereKey.length == 40, "Cohere API key is not 64 characters long")
  implicit val formats: Formats = DefaultFormats

  private case class EmbeddingResponse(id: String, texts: Array[String], embeddings: Array[Array[Double]]) extends java.io.Serializable


  private case class EmbeddingPayload(model: String,
                                      input_type: String,
                                      texts: Seq[String],
                                      truncate: String) extends java.io.Serializable


  implicit val retryStrategy: RetryStrategyProducer = RetryStrategy.fibonacciBackOff(1.seconds, maxAttempts = 11)

  def embed(s: String): Array[Double] = {
    Retry(
      requests.post("https://api.cohere.ai/v1/embed",
        data = write(EmbeddingPayload("embed-english-v3.0", "search_document", Array(s), "END")),
        headers = Map("Authorization" -> s"Bearer $cohereKey", "Content-Type" -> "application/json"))
    ) match {
      case Success(r) => parse(r.text).extract[EmbeddingResponse].embeddings(0)
      case Failure(e) =>
        logger.error(s"Failed to embed string with error: ${e.getMessage}")
        throw e
    }
  }

  def embed(s: Seq[String]): Array[(String, Array[Double])] = {
    Retry(
      requests.post("https://api.cohere.ai/v1/embed",
        data = write(EmbeddingPayload("embed-english-v3.0", "search_document", s, "END")),
        headers = Map("Authorization" -> s"Bearer $cohereKey", "Content-Type" -> "application/json"))
    ) match {
      case Success(r) => parse(r.text).extract[EmbeddingResponse].texts zip parse(r.text).extract[EmbeddingResponse].embeddings
      case Failure(e) =>
        logger.error(s"Failed to embed string with error: ${e.getMessage}")
        throw e
    }
  }
}
