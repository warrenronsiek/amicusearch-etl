package com.amicusearch.etl.read.courtlistener

import com.amicusearch.etl.AppParams
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, SparkSession}

object GenericCourtlisterReader {

  def apply(path: String, env: AppParams.Environment.Value, schema: StructType)(implicit spark: SparkSession): Unit => DataFrame = {
    _ => env match {
        case AppParams.Environment.local => spark.read.schema(schema).json(path)
        case AppParams.Environment.cci => spark.read.schema(schema).json(path)
        case AppParams.Environment.dev => spark.read.schema(schema).option("header", "true").csv(path)
        case AppParams.Environment.prod => spark.read.schema(schema).option("header", "true").csv(path)
      }
  }
}
