package com.amicusearch.etl.process.courtlistener.opinions

import org.scalatest.flatspec.AnyFlatSpec
import com.amicusearch.etl.process.courtlistener.opinions.RemoveTrivialOpinions
import com.amicusearch.etl.utils.NLPParser

class TrivialOpinionsTest extends AnyFlatSpec {

  val trivialOpinion = """On petition for writ of certiorari to the United States Court of Appeals for the Second Circuit. Petition for writ of certiorari granted. Judgment vacated, and case remanded to the United States Court of Appeals for the Second Circuit for further consideration in light of the National Defense Authorization Act for Fiscal Year 2020, Pub. L. No. 116- ---- (S. 1790)."""

  val parsed: NLPParser = NLPParser(trivialOpinion)

  "TrivialOpinions" should "detect trivial opinions" in {
    assert(RemoveTrivialOpinions.isTrivial(parsed.lemmaIterator))
  }
}
