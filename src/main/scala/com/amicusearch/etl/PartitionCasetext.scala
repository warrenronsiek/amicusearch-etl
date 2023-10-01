package com.amicusearch.etl

import com.amicusearch.etl.read.ReadRawCasetext
import org.apache.spark.SparkContext
import org.apache.spark.sql.{DataFrame, SQLContext, SparkSession}

object PartitionCasetext {
  implicit val spark: SparkSession = SparkSession.builder.getOrCreate()
  implicit val sc: SparkContext = spark.sparkContext
  implicit val sql: SQLContext = spark.sqlContext

  val processCasetext: (String, AppParams.Environment.Value) => Unit => DataFrame =
    (path: String, env: AppParams.Environment.Value) => _ => {
      (ReadRawCasetext(path, env))(spark)()
    }

}
