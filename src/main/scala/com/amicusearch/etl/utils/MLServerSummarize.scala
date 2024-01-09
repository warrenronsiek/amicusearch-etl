package com.amicusearch.etl.utils

import com.amicusearch.etl.AppParams
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory
import scala.concurrent.duration._
import util.retry.blocking.{Failure, Retry, RetryStrategy, Success}
import upickle.default._
import upickle.default.{macroRW, ReadWriter => RW}
import util.retry.blocking.RetryStrategy.RetryStrategyProducer

class MLServerSummarize(env: AppParams.Environment.Value, summaryUrl: String) extends java.io.Serializable {
  private case class Response(summary: String) extends java.io.Serializable

  private object Response {
    implicit val rw: RW[Response] = macroRW
  }

  private case class Request(text: String) extends java.io.Serializable

  private object Request {
    implicit val rw: RW[Request] = macroRW
  }

  implicit val retryStrategy: RetryStrategyProducer = RetryStrategy.fibonacciBackOff(0.5.seconds, maxAttempts = 30)

  val summarize: String => String = (text: String) => {
    env match {
      case AppParams.Environment.prod | AppParams.Environment.dev =>
        val req = requests.post(summaryUrl, headers = Map("Content-Type" -> "application/json"), data = write(Request(text)))
        read[Response](req.text).summary
        Retry(requests.post(summaryUrl, headers = Map("Content-Type" -> "application/json"),
          data = write(Request(text)))
        ) match {
          case Success(r) => read[Response](r.text).summary
          case Failure(e) =>
            val logger = LoggerFactory.getLogger("MLServerSummarize")
            logger.error(s"Failed to summarize with error: ${e.getMessage}")
            throw e
        }
      case _ => read[Response]("""{"summary":"stub"}""").summary
    }
  }

}


object MLServerSummarize {
  def apply(env: AppParams.Environment.Value, summaryUrl: String): MLServerSummarize = {
    new MLServerSummarize(env, summaryUrl)
  }
}
