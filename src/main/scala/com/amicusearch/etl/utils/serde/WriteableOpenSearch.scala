package com.amicusearch.etl.utils.serde

import scala.reflect.runtime.universe._
import scala.reflect.runtime.{currentMirror => cm}

trait WriteableOpenSearch {
  def id_str: String

  def parent_id: Option[Long]

  def ignoreFields: Option[Set[String]] = None

  def toJSON: String = {
    val mirror = cm.reflect(this)
    val fields = mirror.symbol.typeSignature.members.collect {
      case m: MethodSymbol if m.isCaseAccessor => m
    }.toList

    // The reason we have to do this bullshit is because npickle has this weird thing where it casts optional types to
    // empty arrays which is obviously retarded. Json4s doesn't work because its not serializeable in Spark.
    val jsonFields: Seq[String] = fields.flatMap { field =>
      val fieldValue = mirror.reflectMethod(field).apply()
      if (ignoreFields.getOrElse(Set()).contains(field.name.toString)) {
        None
      } else {
        fieldValue match {
          case arr: Array[Double] =>
            Some(s""""${field.name.toString}": [${arr.mkString(", ")}]""")
          case n: Number =>
            Some(s""""${field.name.toString}": $n""")
          case _ =>
            Some(s""""${field.name.toString}": "${fieldValue.toString}"""")
        }
      }
    }

    val parentChildMap: String = this.parent_id match {
      case Some(id) => s""""opinion_to_embedding": {"name": "embedding", "parent": "$id"}"""
      case None => """"opinion_to_embedding": {"name": "opinion"}"""
    }

    "{" + (jsonFields :+ parentChildMap).mkString(", ") + "}"
  }
}