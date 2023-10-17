package com.amicusearch.etl.utils

import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.sql.streaming.{OutputMode, Trigger}
import org.apache.spark.sql.{Dataset, SQLContext, SaveMode, SparkSession}

class ParquetWriter(writePath: String, partitionCols: List[String])
                   (implicit spark: SparkSession, sqlContext: SQLContext) extends LazyLogging {
  val write: Dataset[_] => Unit = ds => {
    ds.writeStream
      .partitionBy(partitionCols: _*)
      .outputMode(OutputMode.Append())
      .format("parquet")
      .option("path", writePath)
      .start()
  }
}

object ParquetWriter {
  def apply(writePath: String, partitionCols: List[String])
           (implicit spark: SparkSession, sqlContext: SQLContext): ParquetWriter =
    new ParquetWriter(writePath, partitionCols)

}