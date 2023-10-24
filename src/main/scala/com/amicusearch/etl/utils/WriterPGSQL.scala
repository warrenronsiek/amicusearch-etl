package com.amicusearch.etl.utils

import org.apache.spark.sql.{DataFrame, Dataset}

class WriterPGSQL(url: String, user: String, password: String, tableName: String, timeOut: Option[Long] = None) {

  val props = new java.util.Properties()
  props.setProperty("user", user)
  props.setProperty("password", password)

  val write: Dataset[_] => Unit = df => {
    val ws = df.writeStream.foreachBatch((df: Dataset[_], _: Long) => {
      df.write.mode("append").jdbc(url, tableName, props)
    })
    timeOut match {
      case Some(t) => ws.start().awaitTermination(t)
      case None => ws.start().awaitTermination()
    }
  }
}

object WriterPGSQL {
  def apply(url: String, user: String, password: String, tableName:String,
            timeOut: Option[Long] = None): WriterPGSQL =
    new WriterPGSQL(url, user, password, tableName, timeOut)
}