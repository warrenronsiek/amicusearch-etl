package com.amicusearch.etl.utils

import com.amicusearch.etl.AppParams
import upickle.default.macroRW
import util.retry.blocking.{Failure, Retry, RetryStrategy, Success}
import upickle.default.{macroRW, ReadWriter => RW}
import upickle.default._
import util.retry.blocking.RetryStrategy.RetryStrategyProducer
import scala.concurrent.duration._


class MLServerGetCitations(env: AppParams.Environment.Value, citationsUrl: String) extends java.io.Serializable {


  case class Citation(full:String, cite_type: String) extends java.io.Serializable

  object Citation {
    implicit val rw: RW[Citation] = macroRW
  }

  case class Response(citations: List[Citation]) extends java.io.Serializable

  object Response {
    implicit val rw: RW[Response] = macroRW
  }

  case class Request(text: String) extends java.io.Serializable

  object Request {
    implicit val rw: RW[Request] = macroRW
  }

  implicit val retryStrategy: RetryStrategyProducer = RetryStrategy.fibonacciBackOff(0.5.seconds, maxAttempts = 30)

  val getCitations: String => List[String] = (text: String) => {
    env match {
      case AppParams.Environment.prod | AppParams.Environment.dev =>
        Retry(requests.post(citationsUrl, headers = Map("Content-Type" -> "application/json"),
          data = write(Request(text)))
        ) match {
          case Success(r) => read[Response](r.text).citations.map(_.full)
          case Failure(e) =>
            val logger = org.slf4j.LoggerFactory.getLogger("MLServerGetCitations")
            logger.error(s"Failed to summarize with error: ${e.getMessage}")
            throw e
        }
      case _ => read[Response]("""{"citations":[{"cite_type": "FullLawCitation", "full": "42 U.S.C. \\u00a7 1983", "reporter": "U.S.C.", "section": "1983", "title": "42"}]}""").citations.map(_.full)
    }
  }
}

object MLServerGetCitations {
  def apply(env: AppParams.Environment.Value, citationsUrl: String): MLServerGetCitations = new MLServerGetCitations(env, citationsUrl)
}
