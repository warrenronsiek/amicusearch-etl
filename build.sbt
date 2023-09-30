ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "AmicusearchETL",
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % "3.3.1",
      "org.apache.spark" %% "spark-sql" % "3.3.1",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
      "com.typesafe" % "config" % "1.4.1",
      "com.github.scopt" %% "scopt" % "4.1.0",
      "com.github.pathikrit" %% "better-files" % "3.9.1",
      "net.ruippeixotog" %% "scala-scraper" % "3.1.0",
      "org.scalatest" %% "scalatest" % "3.2.3" % Test, // higher levels can conflict with spark
      "ch.qos.logback" % "logback-classic" % "1.2.3" % Test,
      "ch.qos.logback" % "logback-core" % "1.2.3" % Test,
    )
  )

assembly / test  := {}
assembly / assemblyJarName  := "AmicusearchETL.jar"
assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}