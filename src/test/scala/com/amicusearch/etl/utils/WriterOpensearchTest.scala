package com.amicusearch.etl.utils

import com.amicusearch.etl.utils.serde.{TestDatum, TestDatumChild}
import com.amicusearch.etl.{AppParams, GenericAmicusearchTest}
import org.apache.spark.sql.{Dataset, SaveMode}
import org.apache.spark.sql.types._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._

import scala.util.Try

class WriterOpensearchTest extends AnyFlatSpec with GenericAmicusearchTest with BeforeAndAfterAll {


  private val indexName = "test"

  import sparkSession.implicits._


  val writer: WriterOpensearch[TestDatum] =
    WriterOpensearch[TestDatum](AppParams.Environment.local,
      "https://localhost:9200", "admin", "admin", indexName, Some(1000))

  val writerChild: WriterOpensearch[TestDatumChild] =
    WriterOpensearch[TestDatumChild](AppParams.Environment.local,
      "https://localhost:9200", "admin", "admin", indexName, Some(1000))


  override def beforeAll(): Unit = {
    Try {
      requests.delete(s"https://localhost:9200/$indexName", verifySslCerts = false, auth = ("admin", "admin"))
    }
    List(
      TestDatum(1, "test data"),
      TestDatum(2, "data 2")
    ).toDF().write.mode(SaveMode.Overwrite).parquet("/tmp/testdata")
    val stream = sparkSession.readStream.schema(StructType(Array(StructField("id", LongType), StructField("data", StringType))))
      .parquet("/tmp/testdata").as[TestDatum]
    writer.write(stream)
    List(
      TestDatumChild("3", Array(1.0, 2.0), 1),
      TestDatumChild("4", Array(5.0, 6.0), 2)
    ).toDF().write.mode(SaveMode.Overwrite).parquet("/tmp/testdatachild")
    val streamChild: Dataset[TestDatumChild] = sparkSession.readStream.schema(StructType(Array(StructField("id", StringType), StructField("vector", ArrayType(DoubleType)), StructField("opinionId", LongType))))
      .parquet("/tmp/testdatachild").as[TestDatumChild]
    writerChild.write(streamChild)
    requests.post(s"https://localhost:9200/$indexName/_refresh", verifySslCerts = false, auth = ("admin", "admin"))
  }

  // Cant get CCI to work with opensearch so I have to skip these tests

  "writer" should "write to opensearch" ignore {
    val r = requests.get(s"https://localhost:9200/$indexName/_doc/1", verifySslCerts = false, auth = ("admin", "admin"))
    r.statusCode should be(200)
    r.text() should include(""""found":true""")
  }

  it should "write to opensearch with parent" ignore {
    val r = requests.get(s"https://localhost:9200/$indexName/_doc/3", verifySslCerts = false, auth = ("admin", "admin"))
    r.statusCode should be(200)
    r.text() should include(""""found":true""")
  }
}
