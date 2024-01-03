package com.amicusearch.etl.utils.serde

import scala.reflect.runtime.universe._
import scala.reflect.runtime.{currentMirror => cm}

case class TestDatum(id: Long, data: String) extends WriteableOpenSearch {

  override def id_str: String = id.toString
  override def parent_id: Option[Long] = None

  override def ignoreFields: Option[Set[String]] = Some(Set("id"))

}