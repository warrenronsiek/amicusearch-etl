package com.amicusearch.etl.process.courtlistener.opinions

import org.scalatest.flatspec.AnyFlatSpec
import com.amicusearch.etl.process.courtlistener.opinions.RemoveTrivialOpinions
import com.amicusearch.etl.utils.NLPParser

class TrivialOpinionsTest extends AnyFlatSpec {

  val trivialOpinions = List(
  """On petition for writ of certiorari to the United States Court of Appeals for the Second Circuit. Petition for writ of certiorari granted. Judgment vacated, and case remanded to the United States Court of Appeals for the Second Circuit for further consideration in light of the National Defense Authorization Act for Fiscal Year 2020, Pub. L. No. 116- ---- (S. 1790)."""
  , """BOOTH, Judge, dissenting: I dissent. Crews v. Town of Bay Harbor Islands, 378 So.2d 1265 (Fla. 1st DCA 1979)."""
  , """PLEUS, J. AFFIRMED. See Lyons v. State, 807 So.2d 709 (Fla. 5th DCA 2002). THOMPSON, C.J., and SAWAYA, J„ concur.""")

  val parsed: Seq[Iterator[String]] = trivialOpinions.map(NLPParser(_).lemmaIterator)

  "TrivialOpinions" should "detect trivial opinions" in {
    assert(parsed.map(RemoveTrivialOpinions.isTrivial(_)).reduce(_ && _))
  }
}
