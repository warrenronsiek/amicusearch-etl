package com.amicusearch.etl
import com.amicusearch.etl.utils.USRegion
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

  val paramsCourtlistener: AppParams =
    AppParams(Array("--mode", "courtlistener", "--env", "local", "--states", "FL,NY", "--includeFederal", "true"))

  "AppParamsCourtlistener" should "parse states arguments" in {
    paramsCourtlistener.states shouldEqual List(USRegion.florida, USRegion.new_york)
  }

  it should "parse includeFederal arguments" in {
    paramsCourtlistener.includeFederal shouldEqual true
  }

  it should "parse env var arguments" in {
    paramsCourtlistener.env shouldEqual AppParams.Environment.local
  }

  it should "parse mode arguments" in {
    paramsCourtlistener.mode shouldEqual AppParams.Mode.courtListener
  }
}
