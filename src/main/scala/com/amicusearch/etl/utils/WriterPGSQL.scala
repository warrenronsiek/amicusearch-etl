package com.amicusearch.etl.utils

import org.apache.spark.sql.{DataFrame, Dataset}

class WriterPGSQL(url: String, user: String, password: String, tableName: String, columnCastStmt: Option[String] = None,
                  timeOut: Option[Long] = None) {

  val props = new java.util.Properties()
  props.setProperty("user", user)
  props.setProperty("password", password)

  val write: Dataset[_] => Unit = df => {
    val ws = df.writeStream.foreachBatch((df: Dataset[_], _: Long) => {
      df.write.mode("append").jdbc(url, tableName, props)
    })
    val ws2 = columnCastStmt match {
      case Some(s) => ws.option("createTableColumnTypes", s)
      case None => ws
    }
    timeOut match {
      case Some(t) => ws2.start().awaitTermination(t)
      case None => ws2.start().awaitTermination()
    }
  }
}

object WriterPGSQL {
  def apply(url: String, user: String, password: String, tableName:String, columnCastStmt: Option[String] = None,
            timeOut: Option[Long] = None): WriterPGSQL =
    new WriterPGSQL(url, user, password, tableName, columnCastStmt, timeOut)
}