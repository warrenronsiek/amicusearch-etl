package com.amicusearch.etl.read

import com.amicusearch.etl.AppParams
import org.apache.spark.sql.{DataFrame, SQLContext, SparkSession}
import org.apache.spark.sql.types._


object ReadCourtsDB {

  val schema: StructType = StructType(Array(
    StructField("case_types", ArrayType(StringType), nullable = true),
    StructField("citation_string", StringType, nullable = true),
    StructField("court_url", StringType, nullable = true),
    // dates is an array of maps, which has arbrary values. Diffult to turn into a coulmn
    StructField("dates", ArrayType(StringType), nullable = true),
    StructField("examples", ArrayType(StringType), nullable = true),
    StructField("id", StringType, nullable = true),
    StructField("jurisdiction", StringType, nullable = true),
    StructField("level", StringType, nullable = true),
    StructField("location", StringType, nullable = true),
    StructField("locations", IntegerType, nullable = true),
    StructField("name", StringType, nullable = true),
    StructField("name_abbreviation", StringType, nullable = true),
    StructField("regex", ArrayType(StringType), nullable = true),
    StructField("system", StringType, nullable = true),
    StructField("type", StringType, nullable = true),
  ))

  def apply(path: String)(implicit spark: SparkSession): Unit => DataFrame = {
    _ => spark.read.schema(schema).option("multiline", "true").json(path)
      .withColumnRenamed("type", "court_type")
  }

}
