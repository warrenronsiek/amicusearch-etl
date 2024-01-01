package com.amicusearch.etl.utils

import com.amicusearch.etl.AppParams
import com.typesafe.scalalogging.LazyLogging
import org.apache.hadoop.shaded.org.codehaus.jackson.map.ObjectMapper
import org.apache.spark
import org.apache.spark.sql.streaming.DataStreamWriter
import org.apache.spark.sql.{DataFrame, Dataset, Row, SQLContext, SparkSession}
import upickle.default._
import upickle.default.{Writer, macroRW, ReadWriter => RW}

class WriterOpensearch[T <: WriteableOpenSearch](env: AppParams.Environment.Value, url: String, user: String, password: String, indexName: String, timeOut: Option[Long] = None)
                         (implicit spark: SparkSession, SQLContext: SQLContext)
  extends LazyLogging with java.io.Serializable {


  val write: Dataset[T] => Unit = df => {
    import upickle.default._

    val ws: DataStreamWriter[T] = df.writeStream.foreachBatch((df: Dataset[T], _: Long) => {
      df.foreachPartition((partition: Iterator[T]) => {
        val processed: Iterator[Int] = partition.grouped(100).map((rows: Seq[T]) => {
          val bulkPayload: String = rows.map((row: T) => {
            val metadataJson: String = f"""{"index":{"_index":"$indexName","_id":${row.id}}}"""
            metadataJson + "\n" + row.toJSON + "\n"
          }).mkString("")
          logger.info(bulkPayload)
          val _ = requests.post(
            url + "/_bulk",
            verifySslCerts = env == AppParams.Environment.prod || env == AppParams.Environment.dev,
            headers = Map("Accept" -> "application/x-ndjson", "Content-Type" -> "application/x-ndjson"),
            data = bulkPayload,
            auth = (user, password))
          rows.length
        })
        logger.info("Wrote " + processed.sum + " rows to " + indexName)
      })
    })
    timeOut match {
      case Some(t) => ws.start().awaitTermination(t)
      case None => ws.start().awaitTermination()
    }
  }

}

object WriterOpensearch {
  def apply[T <: WriteableOpenSearch](env: AppParams.Environment.Value, url: String, user: String, password: String, indexName: String,
               timeOut: Option[Long] = None)(implicit spark: SparkSession, SQLContext: SQLContext): WriterOpensearch[T] =
    new WriterOpensearch[T](env, url, user, password, indexName, timeOut)
}
