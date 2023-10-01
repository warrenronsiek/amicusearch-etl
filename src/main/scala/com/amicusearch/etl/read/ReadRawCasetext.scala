package com.amicusearch.etl.read

import com.amicusearch.etl.AppParams
import org.apache.spark.sql.{DataFrame, SQLContext, SparkSession}
import org.apache.spark.sql.types._


object ReadRawCasetext {

  val schema: StructType = StructType(Array(
    StructField("type", StringType, nullable = true),
    StructField("document", StringType, nullable = true),
    StructField("title", StringType, nullable = true),
    StructField("court", StringType, nullable = true),
    StructField("date", StringType, nullable = true),
    StructField("citation", StringType, nullable = true),
    StructField("url", StringType, nullable = true),
  ))

  def apply(path: String, env: AppParams.Environment.Value)(implicit spark: SparkSession): Unit => DataFrame = {
    env match {
      case AppParams.Environment.local => _ => spark.read.schema(schema).json(path)
      case AppParams.Environment.cci => _ =>  spark.read.schema(schema).json(path)
      case AppParams.Environment.dev => _ => spark.readStream.schema(schema).parquet(path)
      case AppParams.Environment.prod => _ => spark.readStream.schema(schema).parquet(path)
    }
  }
}
