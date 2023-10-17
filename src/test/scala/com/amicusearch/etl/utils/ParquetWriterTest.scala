package com.amicusearch.etl.utils

import com.amicusearch.etl.GenericAmicusearchTest
import org.apache.spark
import org.apache.spark.sql.DataFrame
import org.scalatest.flatspec.AnyFlatSpec
import com.amicusearch.etl.read.courtlistener.ReadCourtListenerCourts
import better.files.{File => SFile}

import scala.util.{Failure, Try}

class ParquetWriterTest extends AnyFlatSpec with GenericAmicusearchTest {

  val ct: DataFrame = sparkSession.readStream.schema(ReadCourtListenerCourts.schema)
    .json("src/test/resources/sample_dir/")
  val outDir: String = "/tmp/testout/"

  def writeOutput(testCode: Unit => Unit): Unit = {
    Try {
      SFile(outDir).delete()
    }
    SFile(outDir).createDirectoryIfNotExists()
    val writer = ParquetWriter(outDir, List())
    testCode(writer.write(ct))
  }

  "writer" should "output stream" in writeOutput {  _ =>
    val df = sparkSession.read.schema(ReadCourtListenerCourts.schema).parquet(outDir)
    assertSnapshot("ParquetWriterTest", df.coalesce(1), "id")
  }

}
