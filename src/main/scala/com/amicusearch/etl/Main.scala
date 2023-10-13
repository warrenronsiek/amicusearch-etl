import com.amicusearch.etl.{AppParams, RunCourtlistener, SparkConf}
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.SparkContext
import org.apache.spark.sql.{SQLContext, SparkSession}

class Main extends LazyLogging {
  def run(params: AppParams): Unit = {
    implicit val spark: SparkSession = SparkSession.builder
      .config(SparkConf.conf).getOrCreate()
    implicit val sc: SparkContext = spark.sparkContext
    implicit val sql: SQLContext = spark.sqlContext
    val conf: Config = ConfigFactory.load(params.env match {
      case AppParams.Environment.prod => "prod.conf"
      case AppParams.Environment.dev => "dev.conf"
      case AppParams.Environment.local => "local.conf"
      case AppParams.Environment.cci => "cci.conf"
    })

    params.mode match {
      case AppParams.Mode.courtListener => RunCourtlistener(params, conf)
      case _ => logger.error("Invalid mode")
    }
  }

  def main(args: Array[String]): Unit = {
    run(AppParams(args))
  }
}
