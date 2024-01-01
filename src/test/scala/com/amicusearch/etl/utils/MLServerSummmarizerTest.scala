package com.amicusearch.etl.utils

import com.amicusearch.etl.{AppParams, GenericAmicusearchTest}
import org.scalatest.flatspec.AnyFlatSpec

class MLServerSummmarizerTest extends AnyFlatSpec with GenericAmicusearchTest{
  "Summarizer" should "summarize text" in {
    val summarizer = MLServerSummarize(AppParams.Environment.local, "foo")
    val summary = summarizer.summarize("This is a short opinion.")
    assert(summary == "stub")
  }
}
