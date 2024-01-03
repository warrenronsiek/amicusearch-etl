package com.amicusearch.etl.utils

import com.amicusearch.etl.AppParams
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._
import util.retry.blocking.{Failure, Retry, RetryStrategy, Success}
import org.json4s._
import util.retry.blocking.RetryStrategy.RetryStrategyProducer
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization.write
class MLServerSummarize(env: AppParams.Environment.Value, summaryUrl: String) extends java.io.Serializable with LazyLogging {

  implicit val formats: Formats = DefaultFormats
  private case class Response(summary: String) extends java.io.Serializable


  private case class Request(text: String) extends java.io.Serializable


  implicit val retryStrategy: RetryStrategyProducer = RetryStrategy.fibonacciBackOff(0.5.seconds, maxAttempts = 30)

  val summarize: String => String = (text: String) => {
    env match {
      case AppParams.Environment.prod | AppParams.Environment.dev =>
        Retry(requests.post(summaryUrl, headers = Map("Content-Type" -> "application/json"),
          data = write(Request(text)))
        ) match {
          case Success(r) => parse(r.text).extract[Response].summary
          case Failure(e) =>
            logger.error(s"Failed to summarize with error: ${e.getMessage}")
            throw e
        }
      case _ => parse("""{"summary":"stub"}""").extract[Response].summary
    }
  }
}

object MLServerSummarize {
  def apply(env: AppParams.Environment.Value, summaryUrl: String): MLServerSummarize = {
    new MLServerSummarize(env, summaryUrl)
  }
}
