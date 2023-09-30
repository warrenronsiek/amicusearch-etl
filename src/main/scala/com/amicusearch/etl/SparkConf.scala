package com.amicusearch.etl

import org.apache.spark.SparkConf

object SparkConf {
  val conf = new SparkConf()
    .set("spark.sql.files.maxRecordsPerFile", "20000000")
    .set("spark.sql.parquet.binaryAsString", "true")
    .set("spark.sql.session.timeZone", "UTC")
}
