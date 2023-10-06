import com.amicusearch.etl.read.ReadCourtsDB.schema
import org.apache.spark.sql.functions._
import scopt.OParser
import org.apache.spark.sql.{DataFrame, SQLContext, SparkSession}
import org.apache.spark.sql.types._
import better.files._

val spark = SparkSession.builder().master("local[*]").appName("ReadCourtsDB").getOrCreate()
//val inputFiles = File("/Users/warrenronsiek/Projects/amicusearch-etl/opinions_chunked").list
//
//inputFiles.map(file => {
//  val df = spark.read
//    .parquet(file.pathAsString)
//  (file.name, df.schema)
//}).reduce((a, b) => {
//  if (a._2 != b._2) {
//    println(s"Schema mismatch for ${a._1} and ${b._1}")
//  }
//  (a._1, a._2)
//})

//val dfbig = spark.read.option("mergeSchema", "true").parquet("/Users/warrenronsiek/Projects/amicusearch-etl/opinions_chunked/")
//dfbig.printSchema()

//val df1 = spark.read.parquet("/Users/warrenronsiek/Projects/amicusearch-etl/opinions_chunked/opinion-chunk-31.parquet")
//df1.printSchema()
//val df2 = spark.read.parquet("/Users/warrenronsiek/Projects/amicusearch-etl/opinions_chunked/")
//df2.printSchema()
////
//df1.schema.toList.map(field => {
//  val fieldName = field.name
//  val fieldType = field.dataType
//  val field2 = df2.schema.toList.find(_.name == fieldName).get
//  val field2Type = field2.dataType
//  if (fieldType != field2Type) {
//    println(s"Type mismatch for $fieldName: $fieldType and $field2Type")
//  }
//})
val df = spark.read
  .parquet("/Users/warrenronsiek/Projects/amicusearch-etl/opinions_chunked/opinion-chunk-0.parquet")

df.show(10)
//df.printSchema()
//
//df.limit(20).write.option("overwrite", "true").json("/Users/warrenronsiek/Projects/amicusearch-etl/src/test/resources/courtlistener_opinions_sample")
//println("Done!")