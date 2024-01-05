package com.amicusearch.etl

import com.amicusearch.etl.datatypes.courtlistener.opensearch.{ConformedEmbedding, ConformedOpinion}
import com.amicusearch.etl.utils.WriterOpensearch
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class OpenSearchInsertionTest extends AnyFlatSpec with GenericAmicusearchTest with Matchers with BeforeAndAfterAll {

  override def beforeAll(): Unit = {
    Try {
      requests.delete("https://localhost:9200/opinions", verifySslCerts = false, auth = ("admin", "admin"))
    }
    val writerOpinion: WriterOpensearch[ConformedOpinion] =
      WriterOpensearch[ConformedOpinion](AppParams.Environment.local,
        "https://localhost:9200", "admin", "admin", "opinions", Some(1000))
    RunCLOpinionInsertion.insertion(
      "src/test/resources/processed_opinions_sample_dir",
      AppParams.Environment.local,
      writerOpinion)
    val writerEmbedding = WriterOpensearch[ConformedEmbedding](AppParams.Environment.local,
      "https://localhost:9200", "admin", "admin", "opinions", Some(100000))
    RunCLOpinionEmbedding.insertion(
      "src/test/resources/processed_opinions_sample_dir",
      AppParams.Environment.local, writerEmbedding)
  }

  "OpenSearchInsertion" should "insert opinions" in {
    val r = requests.get("https://localhost:9200/opinions/_doc/4588464", verifySslCerts = false, auth = ("admin", "admin"))
    r.statusCode should be(200)
    r.text() should include(""""found":true""")
  }

  it should "have the correct insertion counts" in {
    val r = requests.get("https://localhost:9200/opinions/_count", verifySslCerts = false, auth = ("admin", "admin"))
    r.statusCode should be(200)
    r.text() should include(""""count":95""")
  }
}
