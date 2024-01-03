package com.amicusearch.etl.utils

import com.amicusearch.etl.AppParams
import com.amicusearch.etl.utils.serde.{CitationRequest, CitationResponse}
import com.typesafe.scalalogging.LazyLogging
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization.write
import util.retry.blocking.RetryStrategy.RetryStrategyProducer
import util.retry.blocking.{Failure, Retry, RetryStrategy, Success}

import scala.concurrent.duration._


class MLServerGetCitations(env: AppParams.Environment.Value, citationsUrl: String) extends java.io.Serializable with LazyLogging {

  implicit val formats: Formats = DefaultFormats

  implicit val retryStrategy: RetryStrategyProducer = RetryStrategy.fibonacciBackOff(0.5.seconds, maxAttempts = 30)

  val getCitations: String => List[String] = (text: String) => {
    env match {
      case AppParams.Environment.prod | AppParams.Environment.dev =>
        Retry(requests.post(citationsUrl, headers = Map("Content-Type" -> "application/json"),
          data = write(CitationRequest(text)))
        ) match {
          case Success(r) => parse(r.text).extract[CitationResponse].citations.map(_.full)
          case Failure(e) =>
            logger.error(s"Failed to summarize with error: ${e.getMessage}")
            throw e
        }
      case _ => parse("""{"citations":[{"cite_type": "FullLawCitation", "full": "42 U.S.C. \\u00a7 1983", "reporter": "U.S.C.", "section": "1983", "title": "42"}]}""").extract[CitationResponse].citations.map(_.full)
    }
  }
}

object MLServerGetCitations {
  def apply(env: AppParams.Environment.Value, citationsUrl: String): MLServerGetCitations = new MLServerGetCitations(env, citationsUrl)
}
