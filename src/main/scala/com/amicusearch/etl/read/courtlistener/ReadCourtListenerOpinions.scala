package com.amicusearch.etl.read.courtlistener


import com.amicusearch.etl.AppParams
import org.apache.spark.sql.{DataFrame, SQLContext, SparkSession}
import org.apache.spark.sql.types._

object ReadCourtListenerOpinions {

  val schema: StructType = StructType(Array(
    StructField("id", StringType, nullable = true),
    StructField("date_created", StringType, nullable = true),
    StructField("date_modified", StringType, nullable = true),
    StructField("author_str", StringType, nullable = true),
    StructField("per_curiam", StringType, nullable = true),
    StructField("joined_by_str", StringType, nullable = true),
    StructField("type", StringType, nullable = true),
    StructField("sha1", StringType, nullable = true),
    StructField("page_count", StringType, nullable = true),
    StructField("download_url", StringType, nullable = true),
    StructField("local_path", StringType, nullable = true),
    StructField("plain_text", StringType, nullable = true),
    StructField("html", StringType, nullable = true),
    StructField("html_lawbox", StringType, nullable = true),
    StructField("html_columbia", StringType, nullable = true),
    StructField("html_anon_2020", StringType, nullable = true),
    StructField("xml_harvard", StringType, nullable = true),
    StructField("html_with_citations", StringType, nullable = true),
    StructField("extracted_by_ocr", StringType, nullable = true),
    StructField("author_id", StringType, nullable = true),
    StructField("cluster_id", StringType, nullable = true)
  ))

  def apply(path: String, env: AppParams.Environment.Value)(implicit spark: SparkSession): Unit => DataFrame = {
    _ =>
      env match {
        case AppParams.Environment.local => spark.read.schema(schema).json(path)
        case AppParams.Environment.cci => spark.read.schema(schema).json(path)
        case AppParams.Environment.dev => spark.readStream.schema(schema).option("header", "true")
          .option("maxFilesPerTrigger", 10).parquet(path).repartition(512)
        case AppParams.Environment.prod => spark.readStream.schema(schema).option("header", "true")
          .option("maxFilesPerTrigger", 10).parquet(path).repartition(512)
      }
  }
}
