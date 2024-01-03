package com.amicusearch.etl.utils

import com.amicusearch.etl.utils.serde.TestDatum
import com.amicusearch.etl.{AppParams, GenericAmicusearchTest}
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.types._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._

class WriterOpensearchTest extends AnyFlatSpec with GenericAmicusearchTest {


  import sparkSession.implicits._


  val writer: WriterOpensearch[TestDatum] =
    WriterOpensearch[TestDatum](AppParams.Environment.local,
      "https://localhost:9200", "admin", "admin", "opinions", Some(10000))

  def inserted(testCode: Unit => Unit): Unit = {
    List(
      TestDatum(8762089, "test data"),
      TestDatum(2, "data 2")
    ).toDF().write.mode(SaveMode.Overwrite).parquet("/tmp/testdata")
    val stream = sparkSession.readStream.schema(StructType(Array(StructField("id", LongType), StructField("data", StringType))))
      .parquet("/tmp/testdata").as[TestDatum]
    writer.write(stream)
    testCode()
  }

  "writer" should "write to opensearch" in inserted { _ =>
    val r = requests.get("https://localhost:9200/opinions/_doc/8762089", verifySslCerts = false, auth = ("admin", "admin"))
    r.statusCode should be(200)
    r.text() should include(""""found":true""")
  }

}
