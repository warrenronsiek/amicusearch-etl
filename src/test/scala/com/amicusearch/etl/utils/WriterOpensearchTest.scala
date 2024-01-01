package com.amicusearch.etl.utils

import org.scalatest.matchers.should.Matchers._
import com.amicusearch.etl.{AppParams, GenericAmicusearchTest}
import org.scalatest.flatspec.AnyFlatSpec
import org.apache.spark.sql.{Dataset, SaveMode}
import upickle.default.{macroRW, ReadWriter => RW}
import upickle.default._
import org.apache.spark.sql.{DataFrame, SQLContext, SparkSession}
import org.apache.spark.sql.types._


class WriterOpensearchTest extends AnyFlatSpec with GenericAmicusearchTest {


  import sparkSession.implicits._


  val writer: WriterOpensearch[WriterOpensearchTest.TestDatum] =
    WriterOpensearch[WriterOpensearchTest.TestDatum](AppParams.Environment.local,
      "https://localhost:9200", "admin", "admin", "opinions", Some(10000))

  def inserted(testCode: Unit => Unit): Unit = {
    List(
      WriterOpensearchTest.TestDatum(8762089, "test data"),
      WriterOpensearchTest.TestDatum(2, "data 2")
    ).toDF().write.mode(SaveMode.Overwrite).parquet("/tmp/testdata")
    val stream = sparkSession.readStream.schema(StructType(Array(StructField("id", LongType), StructField("data", StringType))))
      .parquet("/tmp/testdata").as[WriterOpensearchTest.TestDatum]
    writer.write(stream)
    testCode()
  }

  "writer" should "write to opensearch" in inserted { _ =>
    val r = requests.get("https://localhost:9200/opinions/_doc/8762089", verifySslCerts = false, auth = ("admin", "admin"))
    r.statusCode should be(200)
    r.text() should include(""""found":true""")
  }

}

object WriterOpensearchTest {
  case class TestDatum(id: Long, data: String) extends WriteableOpenSearch {
    override def parent_id: Option[Long] = None

    override def toJSON: String = write(this)
  }

  object TestDatum {
    implicit val rw: RW[TestDatum] = macroRW
  }
}