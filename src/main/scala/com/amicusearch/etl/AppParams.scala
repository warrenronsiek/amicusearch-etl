package com.amicusearch.etl
import scopt.OParser

case class AppParams(mode: AppParams.Mode.Value = AppParams.Mode.partitionCasetext,
                     env: AppParams.Environment.Value = AppParams.Environment.dev)

object AppParams {
  object Mode extends Enumeration {
    val partitionCasetext, caseProcessor = Value
  }

  object Environment extends Enumeration {
    val dev, prod, local, cci = Value
  }
  
  private val builder = OParser.builder[AppParams]

  private val parser = {
    import builder._
    OParser.sequence(
      programName("AmicusearchETL"),
      head("scopt", "4.1"),
      opt[String]('m', "mode")
        .action((x, c) => c.copy(mode = x match {
          case "partitionCasetext" => AppParams.Mode.partitionCasetext
          case "caseProcessor" => AppParams.Mode.caseProcessor
        }))
        .text("the sub-type of etl you want to run"),
      opt[String]('e', "env")
        .action((x, c) => c.copy(env = x match {
          case "dev" => AppParams.Environment.dev
          case "prod" => AppParams.Environment.prod
          case "local" => AppParams.Environment.local
          case "cci" => AppParams.Environment.cci
        }))
        .text("the environment you want to run in")
    )
  }

  def apply(args: Array[String]): AppParams = {
    OParser.parse(parser, args, AppParams()) match {
      case Some(appParams: AppParams) => appParams
      case _ => throw new IllegalArgumentException("Invalid arguments")
    }
  }

}
