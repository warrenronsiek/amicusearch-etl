package com.amicusearch.etl
import com.amicusearch.etl.AppParams
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class AppParamsTest extends AnyFlatSpec with Matchers {
  val params: AppParams = AppParams(Array("--env", "local", "--mode", "partitionCasetext"))

  "AppParams" should "parse env var arguments" in {
    params.env shouldEqual AppParams.Environment.local
  }

  it should "parse mode arguments" in {
    params.mode shouldEqual AppParams.Mode.partitionCasetext
  }
}
