package com.amicusearch.etl.utils

import com.amicusearch.etl.GenericAmicusearchTest
import com.amicusearch.etl.read.courtlistener.ReadProcessedOpinions
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.types.{ArrayType, DoubleType, IntegerType, StringType, StructField, StructType}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.flatspec._
import org.scalatest.matchers.should.Matchers._

import java.sql.{Connection, DriverManager, ResultSet}

class WriterPGSQLTest extends AnyFlatSpec with GenericAmicusearchTest {
  val url = "jdbc:postgresql://localhost:5432/postgres"
  val username = "postgres"
  val password = "postgres"
  val conn: Connection = DriverManager.getConnection(url, username, password)

  def inserted(testCode: Unit => Unit): Unit = {
    conn.createStatement.execute("DROP TABLE IF EXISTS test")
    val writer: WriterPGSQL = WriterPGSQL(url, username, password, "test", Some(10000))
    writer.write(processedOpinionStream)
    testCode()
  }

  "writer" should "write to pg" in inserted {_ =>
    val resultSet = conn.createStatement.executeQuery("SELECT * FROM test")
    resultSet.next() should be(true)
  }

  def typeCastInserts(testCode: Unit => Unit): Unit = {
    conn.createStatement.execute("DROP TABLE IF EXISTS test_typed_inserts")
    val dummyData = sparkSession.readStream
      .schema(StructType(Array(StructField("id", StringType), StructField("vec", ArrayType(DoubleType)))))
      .json("src/test/resources/dummy_array_data/")
    val writer: WriterPGSQL = WriterPGSQL(url, username, password, "test_typed_inserts", Some(10000))
    writer.write(dummyData)
    testCode()
  }

  it should "type cast inserts" in typeCastInserts {_ =>
    val resultSet = conn.createStatement.executeQuery("SELECT * FROM test_typed_inserts")
    resultSet.next() should be(true)
  }

}
