package com.amicusearch.etl
import com.amicusearch.etl.utils.USRegion
import scopt.OParser

case class AppParams(mode: AppParams.Mode.Value = AppParams.Mode.partitionCasetext,
                     env: AppParams.Environment.Value = AppParams.Environment.dev,
                     states: List[USRegion.Value] = List.empty[USRegion.Value],
                     includeFederal: Boolean = false, summarize: Boolean = true)

object AppParams {
  object Mode extends Enumeration {
    val partitionCasetext, CLOpinionProcessor, CLOpinionInsert, CLOpinionEmbed = Value
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
          case "CLOpinionProcessor" => AppParams.Mode.CLOpinionProcessor
          case "CLOpinionInsert" => AppParams.Mode.CLOpinionInsert
          case "CLOpinionEmbed" => AppParams.Mode.CLOpinionEmbed
        }))
        .text("the sub-type of etl you want to run"),
      opt[String]('e', "env")
        .action((x, c) => c.copy(env = x match {
          case "dev" => AppParams.Environment.dev
          case "prod" => AppParams.Environment.prod
          case "local" => AppParams.Environment.local
          case "cci" => AppParams.Environment.cci
        }))
        .text("the environment you want to run in"),
      opt[String]('s', "states")
        .action((x, c) => c.copy(states = x.split(",").toList.map(USRegion.fromString(_).get)))
        .text("the states you want to run in"),
      opt[Boolean]('f', "includeFederal")
        .action((x, c) => c.copy(includeFederal = x))
        .text("whether to include federal courts"),
      opt[Boolean]('s', "summarize")
        .action((x, c) => c.copy(summarize = x))
        .text("whether to summarize the opinions")
    )
  }

  def apply(args: Array[String]): AppParams = {
    OParser.parse(parser, args, AppParams()) match {
      case Some(appParams: AppParams) => appParams
      case _ => throw new IllegalArgumentException("Invalid arguments")
    }
  }

}
