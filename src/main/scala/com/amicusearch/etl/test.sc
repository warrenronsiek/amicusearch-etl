import com.amicusearch.etl.read.ReadCourtsDB.schema
import org.apache.spark.sql.functions._
import scopt.OParser
import org.apache.spark.sql.{DataFrame, SQLContext, SparkSession}
import org.apache.spark.sql.types._
import better.files._

val spark = SparkSession.builder().master("local[*]").appName("ReadCourtsDB").getOrCreate()
val df = spark.read.parquet("/Users/warrenronsiek/Projects/amicusearch-etl/src/test/resources/processedopinions/")
df.printSchema()
df.limit(20).write.json("/Users/warrenronsiek/Projects/amicusearch-etl/src/test/resources/processedopinionssample/")
