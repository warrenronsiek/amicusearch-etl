package com.amicusearch.etl.read

import com.amicusearch.etl.{AppParams, GenericAmicusearchTest}
import org.scalatest.flatspec.AnyFlatSpec
import org.apache.spark.sql.DataFrame

class ReadRawCasetextTest extends AnyFlatSpec with GenericAmicusearchTest {

  val df: DataFrame = ReadRawCasetext("src/test/resources/casetextsample.jsonl", AppParams.Environment.local)
    .apply().toDF().coalesce(1)

  "ReadCasetext" should "read casetext data" in {
    assertSnapshot("ReadCasetext", df, "url")
  }
}
