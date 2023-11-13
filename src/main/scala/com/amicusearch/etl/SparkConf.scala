package com.amicusearch.etl

import org.apache.spark.SparkConf

object SparkConf {

  def sparkConf(checkpointLocation: String): SparkConf = new SparkConf()
    .set("spark.sql.files.maxRecordsPerFile", "30000")
    .set("spark.sql.shuffle.partitions", "2100")
    .set("spark.sql.parquet.binaryAsString", "true")
    .set("spark.sql.session.timeZone", "UTC")
    .set("spark.sql.streaming.checkpointLocation", checkpointLocation)
    .set("driver", "org.postgresql.Driver")
    .set("spark.driver.extraJavaOptions", "-Dlog4j.configuration=file:src/main/resources/log4j.properties")
}
