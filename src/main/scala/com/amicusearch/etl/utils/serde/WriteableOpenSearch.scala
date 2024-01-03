package com.amicusearch.etl.utils.serde

import scala.reflect.runtime.universe._
import scala.reflect.runtime.{currentMirror => cm}

trait WriteableOpenSearch {
  def id_str: String
  def parent_id: Option[Long]

  def toJSON: String = {
    val mirror = cm.reflect(this)
    val fields = mirror.symbol.typeSignature.members.collect {
      case m: MethodSymbol if m.isCaseAccessor => m
    }.toList

    val jsonMap = fields.map { field =>
      val fieldValue = mirror.reflectMethod(field).apply()
      s""""${field.name.toString}": "${fieldValue.toString}""""
    }

    "{" + jsonMap.mkString(", ") + "}"
  }
}
