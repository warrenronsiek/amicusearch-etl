//import com.amicusearch.etl.read.ReadCourtsDB.schema
//import org.apache.spark.sql.functions._
//import scopt.OParser
//import org.apache.spark.sql.{DataFrame, SQLContext, SparkSession}
//import org.apache.spark.sql.types._
//import better.files._
////val spark = SparkSession.builder().master("local[*]").appName("ReadCourtsDB").getOrCreate()

//import edu.stanford.nlp.ling.CoreLabel
//import edu.stanford.nlp.pipeline.{CoreDocument, StanfordCoreNLP}
//
//import java.util.Properties
//import scala.jdk.CollectionConverters._
//
//val text = "This is a short opinion."
//
//// set up pipeline properties
//val props = new Properties();
//// set the list of annotators to run
//props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
//// set a property for an annotator, in this case the coref annotator is being set to use the neural algorithm
//props.setProperty("coref.algorithm", "neural");
//// build pipeline
//val pipeline: StanfordCoreNLP = new StanfordCoreNLP(props);
//// create a document object
//val document = new CoreDocument(text);
//// annnotate the document
//pipeline.annotate(document);
//val doc = document.tokens().asScala.iterator
//
//doc.foreach(println)

val punctuation: Set[Char] = Set(',', '.', '!', '?', ';', ':', '(', ')', '[', ']', '{', '}', '-', '_', '+', '=', '@',
  '#', '$', '%', '^', '&', '*', '|', '/', '\\', '<', '>', '~', '`', '\'', '\"')

punctuation.contains(".")