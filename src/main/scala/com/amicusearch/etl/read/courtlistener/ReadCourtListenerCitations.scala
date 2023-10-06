package com.amicusearch.etl.read.courtlistener

import com.amicusearch.etl.AppParams
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, SparkSession}

object ReadCourtListenerCitations {

  val schema: StructType = StructType(Array(
    StructField("id", StringType, nullable = true),
    StructField("volume", StringType, nullable = true),
    StructField("reporter", StringType, nullable = true),
    StructField("page", StringType, nullable = true),
    StructField("type", StringType, nullable = true),
    StructField("cluster_id", StringType, nullable = true)
  ))

  def apply(path: String, env: AppParams.Environment.Value)(implicit spark: SparkSession): Unit => DataFrame = {
    GenericCourtlisterReader(path, env, schema) andThen
      (df => df.withColumnRenamed("type", "citation_type"))
  }
}
