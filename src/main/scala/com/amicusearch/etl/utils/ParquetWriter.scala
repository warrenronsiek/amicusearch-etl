package com.amicusearch.etl.utils

import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.sql.streaming.{DataStreamWriter, OutputMode, Trigger}
import org.apache.spark.sql.{Dataset, SQLContext, SaveMode, SparkSession}

class ParquetWriter(writePath: String, partitionCols: List[String], terminationTimeout: Option[Long] = None)
                   (implicit spark: SparkSession, sqlContext: SQLContext) extends LazyLogging {
  val write: Dataset[_] => Unit = ds => {
    val v1: DataStreamWriter[_] = ds.writeStream
      .outputMode(OutputMode.Append())
      .trigger(Trigger.AvailableNow())
      .format("parquet")
      .option("basePath", writePath)
      .option("path", writePath)
    val v2: DataStreamWriter[_] = if (partitionCols.nonEmpty) {
      v1.partitionBy(partitionCols: _*)
    } else {
      v1
    }
    terminationTimeout match {
      case Some(timeout) => v2.start().awaitTermination(timeout)
      case None => v2.start().awaitTermination()
    }
  }
}

object ParquetWriter {
  def apply(writePath: String, partitionCols: List[String], terminationTimeout: Option[Long] = None)
           (implicit spark: SparkSession, sqlContext: SQLContext): ParquetWriter =
    new ParquetWriter(writePath, partitionCols, terminationTimeout)

}