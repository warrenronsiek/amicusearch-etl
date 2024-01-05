package com.amicusearch.etl.utils.serde

case class TestDatumChild(id: String, vector: Array[Double], opinionId: Long) extends WriteableOpenSearch {

  override def id_str: String = id
  override def parent_id: Option[Long] = Some(opinionId)

  override def ignoreFields: Option[Set[String]] = Some(Set("opinionId", "id"))


}
