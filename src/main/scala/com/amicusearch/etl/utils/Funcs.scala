package com.amicusearch.etl.utils

object Funcs {

  def parseNan(s: String): Option[String] = s match {
    case "nan" => None
    case "" => None
    case "<absent>" => None
    case s: String => Some(s)
    case _ => None
  }

}
