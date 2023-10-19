package com.amicusearch.etl.utils

import com.amicusearch.etl.AppParams
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration._
import util.retry.blocking.{Failure, Retry, RetryStrategy, Success}
import upickle.default._
import upickle.default.{macroRW, ReadWriter => RW}

class Summarizer(env: AppParams.Environment.Value, summaryUrl: String) extends java.io.Serializable{

  private case class Response(summary: String) extends java.io.Serializable
  private object Response{
    implicit val rw: RW[Response] = macroRW
  }

  private case class Request(text: String) extends java.io.Serializable
  private object Request{
    implicit val rw: RW[Request] = macroRW
  }

  val summarize: String => String = (text:String) => {
    env match {
      case AppParams.Environment.prod | AppParams.Environment.dev =>
        val req = requests.post(summaryUrl, data = write(Request(text)))
        read[Response](req.text).summary
      case _ => read[Response]("""{"summary":"stub"}""").summary
    }
  }
}

object Summarizer {
  def apply(env: AppParams.Environment.Value, summaryUrl: String): Summarizer = {
    new Summarizer(env, summaryUrl)
  }
}
