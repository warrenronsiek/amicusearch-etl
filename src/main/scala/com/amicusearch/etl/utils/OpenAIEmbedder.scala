package com.amicusearch.etl.utils

import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._
import util.retry.blocking.{Failure, Retry, RetryStrategy, Success}
import upickle.default._
import upickle.default.{macroRW, ReadWriter => RW}
object OpenAIEmbedder extends LazyLogging{

  private case class Usage(prompt_tokens: Int, total_tokens: Int)
  private object Usage{
    implicit val rw: RW[Usage] = macroRW
  }
  private case class Datum(embedding: Array[Double], index: Int)
  private object Datum{
    implicit val rw: RW[Datum] = macroRW
  }
  private case class EmbeddingResponse(data:Array[Datum], model: String, usage: Usage)
  private object EmbeddingResponse{
    implicit val rw: RW[EmbeddingResponse] = macroRW
  }

  private case class EmbeddingPayload(model: String, input: String)
  private object EmbeddingPayload{
    implicit val rw: RW[EmbeddingPayload] = macroRW
  }

  implicit val retryStrategy = RetryStrategy.fibonacciBackOff(3.seconds, maxAttempts = Double.PositiveInfinity.toInt)

  def embed(s: String): Array[Double] = {
    Retry(requests.post("https://api.openai.com/v1/embeddings",
      data = write(EmbeddingPayload("text-embedding-ada-002", s)),
      headers = Map("Authorization" -> s"Bearer ${sys.env("OPENAI_API_KEY")}", "Content-Type" -> "application/json"))
    ) match {
      case Success(r) => read[EmbeddingResponse](r.text).data(0).embedding
      case Failure(e) =>
        logger.error(s"Failed to embed string with error: ${e.getMessage}")
        throw e
    }
  }
}
