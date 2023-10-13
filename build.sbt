ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

dependencyOverrides += "org.scala-lang.modules" %% "scala-xml" % "2.1.0"

lazy val root = (project in file("."))
  .settings(
    name := "AmicusearchETL",
    libraryDependencies ++= Seq(
      "com.warren-r" % "sparkutils_2.13" % "0.1.4",
      "com.amazonaws" % "aws-java-sdk" % "1.12.560",
      "org.apache.spark" %% "spark-core" % "3.5.0",
      "org.apache.spark" %% "spark-sql" % "3.5.0",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
      "com.typesafe" % "config" % "1.4.2",
      "com.github.scopt" %% "scopt" % "4.1.0",
      "com.github.pathikrit" %% "better-files" % "3.9.2",
      "net.ruippeixotog" %% "scala-scraper" % "3.1.0",
      "edu.stanford.nlp" % "stanford-corenlp" % "4.5.4",
      "edu.stanford.nlp" % "stanford-corenlp" % "4.5.4" classifier "models",
      "org.scalatest" %% "scalatest" % "3.2.17" % Test,
      "ch.qos.logback" % "logback-classic" % "1.4.7" % Test,
      "ch.qos.logback" % "logback-core" % "1.4.7" % Test,
    )
  )

assembly / test := {}
assembly / assemblyJarName := "AmicusearchETL.jar"
assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}