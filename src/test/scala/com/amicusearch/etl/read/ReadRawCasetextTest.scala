package com.amicusearch.etl.read

import com.amicusearch.etl.{AppParams, GenericAmicusearchTest}
import org.scalatest.flatspec.AnyFlatSpec

class ReadRawCasetextTest extends AnyFlatSpec with GenericAmicusearchTest {

  val df = ReadRawCasetext("src/test/resources/casetextsample.jsonl", AppParams.Environment.local).apply()

  "ReadCasetext" should "read casetext data" in {
    assertSnapshot("ReadCasetext", df, "id")
  }
}
