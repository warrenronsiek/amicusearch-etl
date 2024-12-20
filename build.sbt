ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.18"

dependencyOverrides += "org.scala-lang.modules" %% "scala-xml" % "2.1.0" // spark requires 2.1.0, which is incompatible with sclatest requirement of 1.2.0


lazy val root = (project in file("."))
  .settings(
    name := "AmicusearchETL",
    libraryDependencies ++= Seq(
      "com.warren-r" % "sparkutils_2.12" % "0.1.5" % Test,
      "org.apache.hadoop" % "hadoop-aws" % "3.3.4",
      "com.amazonaws" % "aws-java-sdk" % "1.11.901", // do not update, is a dep with hadoop-aws
      "org.apache.spark" %% "spark-core" % "3.5.0" % Provided,
      "org.apache.spark" %% "spark-sql" % "3.5.0" % Provided,
      "org.apache.logging.log4j" % "log4j-slf4j2-impl" % "2.20.0",
      "org.apache.logging.log4j" % "log4j-api" % "2.20.0",
      "org.apache.logging.log4j" % "log4j-core" % "2.20.0",
      "com.lihaoyi" %% "requests" % "0.8.0",
      "com.lihaoyi" %% "upickle" % "3.1.3",
      "com.github.hipjim" %% "scala-retry" % "0.3.0",
      "com.typesafe" % "config" % "1.4.2",
      "com.github.scopt" %% "scopt" % "4.1.0",
      "com.github.pathikrit" %% "better-files" % "3.9.2",
      "net.ruippeixotog" %% "scala-scraper" % "3.1.0",
      "edu.stanford.nlp" % "stanford-corenlp" % "4.5.4",
      "edu.stanford.nlp" % "stanford-corenlp" % "4.5.4" classifier "models",
      "org.scalatest" %% "scalatest" % "3.2.17" % Test,
      "org.bouncycastle" % "bcprov-jdk15on" % "1.70", // is required to play nice with rancher kubernetes
      "org.bouncycastle" % "bcpkix-jdk15on" % "1.70", // is required to play nice with rancher kubernetes
    )
  )

assembly / test := {}
assembly / assemblyJarName := "AmicusearchETL.jar"
assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard // do not update the ampersand syntax - it breaks
  case x => MergeStrategy.first
}