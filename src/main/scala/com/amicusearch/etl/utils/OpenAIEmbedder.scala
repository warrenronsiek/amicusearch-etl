package com.amicusearch.etl.utils

import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._
import util.retry.blocking.{Failure, Retry, RetryStrategy, Success}
import upickle.default._
import upickle.default.{macroRW, ReadWriter => RW}
import util.retry.blocking.RetryStrategy.RetryStrategyProducer
object OpenAIEmbedder extends LazyLogging with java.io.Serializable{

  private case class Usage(prompt_tokens: Int, total_tokens: Int) extends java.io.Serializable
  private object Usage{
    implicit val rw: RW[Usage] = macroRW
  }
  private case class Datum(embedding: Array[Double], index: Int) extends java.io.Serializable
  private object Datum{
    implicit val rw: RW[Datum] = macroRW
  }
  private case class EmbeddingResponse(data:Array[Datum], model: String, usage: Usage) extends java.io.Serializable
  private object EmbeddingResponse{
    implicit val rw: RW[EmbeddingResponse] = macroRW
  }

  private case class EmbeddingPayload(model: String, input: String) extends java.io.Serializable
  private object EmbeddingPayload{
    implicit val rw: RW[EmbeddingPayload] = macroRW
  }

  implicit val retryStrategy: RetryStrategyProducer = RetryStrategy.fibonacciBackOff(1.seconds, maxAttempts = 11)

  def embed(s: String): Array[Double] = {
    Retry(requests.post("https://api.openai.com/v1/embeddings",
      data = write(EmbeddingPayload("text-embedding-ada-002", s)),
      headers = Map("Authorization" -> s"Bearer ${System.getenv("OPENAI_API_KEY")}", "Content-Type" -> "application/json"))
    ) match {
      case Success(r) => read[EmbeddingResponse](r.text).data(0).embedding
      case Failure(e) =>
        logger.error(s"Failed to embed string with error: ${e.getMessage}")
        throw e
    }
  }
}
