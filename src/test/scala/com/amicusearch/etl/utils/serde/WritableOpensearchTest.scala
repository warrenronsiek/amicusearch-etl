package com.amicusearch.etl.utils.serde

import com.amicusearch.etl.datatypes.courtlistener.opensearch.ConformedOpinion
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class WritableOpensearchTest extends AnyFlatSpec with Matchers {
  case class T1(id: Long, vector: Array[Double]) extends WriteableOpenSearch {
    override def id_str: String = id.toString

    override def parent_id: Option[Long] = None
  }

  case class T2(id: String, vector: Array[Double], opinionId: Long) extends WriteableOpenSearch {
    override def id_str: String = id

    override def parent_id: Option[Long] = Some(opinionId)
  }

  case class T3(id: String, vector: Array[Double], opinionId: Long, ignore: String) extends WriteableOpenSearch {
    override def id_str: String = id

    override def parent_id: Option[Long] = Some(opinionId)

    override def ignoreFields: Option[Set[String]] = Some(Set("ignore"))
  }

  case class T4(id: String, vector: Option[Array[Double]], opinionId: Long, ignore: String, prop1: Option[String] = None) extends WriteableOpenSearch {
    override def id_str: String = id

    override def parent_id: Option[Long] = Some(opinionId)

    override def ignoreFields: Option[Set[String]] = Some(Set("ignore"))
  }


  "WritableOpensearch" should "serialize to json" in {
    T1(1, Array(1.0, 2.0, 3.0)).toJSON should be("""{"vector": [1.0, 2.0, 3.0], "id": 1, "opinion_to_embedding": {"name": "opinion"}}""")
  }

  it should "serialize to json with parent id" in {
    T2("test", Array(1.0, 2.0, 3.0), 1).toJSON should be("""{"opinionId": 1, "vector": [1.0, 2.0, 3.0], "id": "test", "opinion_to_embedding": {"name": "embedding", "parent": 1}}""")
  }

  it should "serialize to json with parent id and ignore fields" in {
    T3("test", Array(1.0, 2.0, 3.0), 1, "ignore").toJSON should be("""{"opinionId": 1, "vector": [1.0, 2.0, 3.0], "id": "test", "opinion_to_embedding": {"name": "embedding", "parent": 1}}""")
  }

  it should "serialize to json with parent id and ignore fields and include optional" in {
    T4("test", Some(Array(1.0, 2.0, 3.0)), 1, "ignore", Some("prop1")).toJSON should be("""{"prop1": "prop1", "opinionId": 1, "vector": [1.0, 2.0, 3.0], "id": "test", "opinion_to_embedding": {"name": "embedding", "parent": 1}}""")
  }

  it should "serialize to json with parent id and ignore fields and exclude None" in {
    T4("test", None, 1, "ignore", Some("prop1")).toJSON should be("""{"prop1": "prop1", "opinionId": 1, "id": "test", "opinion_to_embedding": {"name": "embedding", "parent": 1}}""")
  }

}
