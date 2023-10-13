package com.amicusearch.etl.utils

import com.typesafe.scalalogging.LazyLogging
import org.apache.hadoop.conf.Configuration
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{LongType, StringType}
import org.apache.spark.sql.{Dataset, SQLContext, SaveMode, SparkSession}

class ParquetWriter(writePath: String, partitionCols: List[String], saveMode: SaveMode = SaveMode.Overwrite)
                     (implicit spark: SparkSession, sqlContext: SQLContext) extends LazyLogging {
  val write: Dataset[_] => Unit = ds => {

    val window = Window.partitionBy(partitionCols.head).orderBy(col(partitionCols.head).desc)
    val newPartitions = partitionCols :+ "rand_partition"

    // that we repartition by rand_partition means that we ensure that each file written out contains no more than
    // 5000000 records. This is important because otherwise repartitioning by a large partition will cause OOM
    ds.withColumn("rand_partition", (row_number.over(window) / 500000).cast(LongType))
      .repartition(newPartitions.map(s => col(s)):_*)
      .drop("rand_partition")
      .write
      .mode(saveMode)
      .partitionBy(partitionCols: _*)
      .parquet(writePath)
  }
}

object ParquetWriter {
  def apply(writePath: String, partitionCols: List[String], saveMode: SaveMode = SaveMode.Overwrite)
           (implicit spark: SparkSession, sqlContext: SQLContext): ParquetWriter =
    new ParquetWriter(writePath, partitionCols, saveMode)

}