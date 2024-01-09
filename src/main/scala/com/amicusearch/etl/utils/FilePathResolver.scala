package com.amicusearch.etl.utils

import com.amazonaws.auth.{AWSCredentialsProvider, AWSStaticCredentialsProvider, EnvironmentVariableCredentialsProvider}
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.model.ListObjectsV2Request
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import com.amicusearch.etl.AppParams.Environment
import java.time.LocalDate
import org.slf4j.LoggerFactory
import java.time.format.DateTimeFormatter
import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

/**
 * This objects job is to figure out:
 * 1. If read/write path passed is a remote S3 path, or a local config path
 * 2. If its remote, use passed date arguments to return all valid directory partitions
 * 3. If its local, return the fully qualified file path to the resource in question. Local directory resolution is important for running tests.
 * 4. If it is a versioned s3 path, inject the correct version number into the path
 */
class FilePathResolver(mode: Environment.Value, s3ClientOverride: Option[AmazonS3] = None, version: Option[Int] = None) {
  /**
   * We want to build different clients depending on the context. In particular, we need clients to instantiate
   * differently depending on if they are created locally or in production. We also want to be able to test production
   * functionality locally, so we allow for the possiblity of a clientOverride to be passed for tests. We also want
   * to be able to unit test the functions without having a "production" client. This function allows for all these
   * different use cases. If this seems absurdly complicated, then blame the fact that we have all this "assume role"
   * bullshit that we have to deal with every time we want to do something.
   */
  private val logger = LoggerFactory.getLogger("FilePathResolver")

  private lazy val s3Client: AmazonS3 = (s3ClientOverride, mode) match {
    case (Some(client), _) => client
    case (None, Environment.prod) => AmazonS3ClientBuilder.standard().withRegion(Regions.US_WEST_2).build()
    case (None, Environment.dev) => AmazonS3ClientBuilder.standard().withRegion(Regions.US_WEST_2).build()
    case (None, _) =>
      val creds: AWSCredentialsProvider = {
        new AWSStaticCredentialsProvider(new EnvironmentVariableCredentialsProvider().getCredentials)
      }
      AmazonS3ClientBuilder.standard()
        .withCredentials(creds)
        .withRegion(Regions.US_WEST_2).build()
  }


  private[utils] case class PathResolution(paths: List[String], invalidPathCount: Int) {
    def addPath(path: String): PathResolution = PathResolution(path :: this.paths, this.invalidPathCount)

    def incrementInvalidPathCount(): PathResolution = PathResolution(this.paths, 1 + this.invalidPathCount)

    def +(that: PathResolution): PathResolution = PathResolution(this.paths ++ that.paths,
      this.invalidPathCount + that.invalidPathCount)
  }

  /**
   * This is the meat of the operation. The idea is that given a s3 path e.g. s3://my-bucket/my-key/YYYYMMDD/, a date,
   * and a lookback, we generate all valid s3 directories. So a date of 2020-01-20 and a lookback of 2 would return
   * List(s3://my-bucket/my-key/20200120/, s3://my-bucket/my-key/20200119/) assuming that both those file paths
   * actually exist in S3.
   */
  private[utils] val s3PathDateInjector: (String, Option[LocalDate]) => PathResolution =
    (path, date) => {
      val dateInjected: String = date match {
        case Some(d) => path
          .replace("YYYY", d.format(DateTimeFormatter.ofPattern("yyyy")))
          .replace("MM", d.format(DateTimeFormatter.ofPattern("MM")))
          .replace("DD", d.format(DateTimeFormatter.ofPattern("dd")))
        case None => path // if no date is provided, just return the path string
      }
      PathResolution(List(dateInjected), 0)
    }

  private[utils] def getBucketKey(s3Path: String): (String, String) = {
    val (bucket: String, prefix: String) = s3Path.substring(5).split("/").toList match {
      case hd :: tl => (hd, tl.mkString("/") + {
        if (s3Path.endsWith("/")) "/" else ""
      })
      case _ => throw new Exception("invalid s3 path")
    }
    (bucket, prefix)
  }

  /**
   * When reading from S3, we need to be careful that we only choose directories that actually exist before passing them
   * to spark, otherwise we get an error. We do this by iterating over the list of paths and checking if each path
   * exists. If it doesn't exist, we throw it out and increment the error counter. If it does exist, we keep it.
   */
  private[utils] val s3PathValidator: PathResolution => PathResolution = pathResolution => {
    pathResolution.paths.foldLeft(PathResolution(List[String](), 0))((pathResolution: PathResolution, s3path: String) => {
      // We want to filter out cases where we get invalid S3 paths that do not exist, and then we also want to
      // report metrics on missing s3 directories so we can raise alerts.
      val (bucket: String, prefix: String) = getBucketKey(s3path)
      val pathExists: Boolean = s3Client.listObjectsV2(new ListObjectsV2Request()
        .withBucketName(bucket)
        .withPrefix(prefix).withMaxKeys(2)).getObjectSummaries.asScala.nonEmpty
      if (pathExists) {
        pathResolution.addPath(s3path)
      } else {
        logger.warn("could not resolve s3 path: " + s3path + " you are likely missing important data.")
        pathResolution.incrementInvalidPathCount()
      }
    })
  }

  /**
   * Given an s3 path, we might have multiple versions. We need to identify why one
   * to read/write from by finding the most recent version.
   *
   * @param s3path a s3 path string that contains VERSION to be identified - i.e. s3://mybucekt/mypath/VERSION/foo/bar.txt
   * @return the largest version of the directory that exists for the provided path
   */
  private[utils] def getLatestVersion(s3path: String): Option[Int] = {
    val (bucket, key) = getBucketKey(s3path)
    val keyWithoutVersion = key.replace("VERSION/", "")
    s3Client.listObjectsV2(new ListObjectsV2Request().withBucketName(bucket)
        .withPrefix(keyWithoutVersion)) //TODO: implies that VERSION is the last sub-dir in the string. Might not be true.
      .getObjectSummaries.asScala
      .map(_.getKey.replace(keyWithoutVersion, ""))
      .map(a => a.split("/").head)
      .filter(_.startsWith("v"))
      .map(_.substring(1).toInt) match {
      case l if l.nonEmpty => Some(l.max)
      case _ => None
    }
  }

  /**
   * Depending on whether we read/write, we want the VERSION system to return different values. I.e. if we specify
   * a version to read, we want to read it, otherwise default to latest. However, if we want to write, we need
   * to resolve to a set of different write paths - both "latest" and the newest version.
   */
  object ReadOrWrite extends Enumeration {
    val read, write = Value
  }

  private[utils] val s3PathVersionInjector: ReadOrWrite.Value => PathResolution => PathResolution = readOrWrite => pathResolution => {
    lazy val writeVersion: Int = getLatestVersion(pathResolution.paths.head) match {
      case Some(i) => i + 1
      case None => 0
    } //TODO: potential bug - suppose that there are multiple versions across a particular time range - We need to resolve them individually?
    lazy val versionsToInject: List[String] = (readOrWrite, version) match {
      case (ReadOrWrite.read, Some(v)) => List(s"v$v")
      case (ReadOrWrite.read, None) => List("latest")
      case (ReadOrWrite.write, Some(_)) => throw new Exception("overwriting specified version is not supported")
      case (ReadOrWrite.write, None) => List(s"v$writeVersion", "latest")
    }
    val versionedPaths: List[String] = pathResolution.paths.flatMap(path => versionsToInject.map(v => path.replace("VERSION", v)))
    PathResolution(versionedPaths, pathResolution.invalidPathCount)
  }

  private def handleLocalPath(path: String): List[String] = {
    Try {
      List(getClass.getResource(path).getPath)
    } recover {
      case _: NullPointerException => List()
    } match {
      case Success(value) => value
      case Failure(exception) => throw exception
    }
  }

  // multiple read paths are used to get multiple "process_date" partitions, hence the List[String] return type
  def readPath(path: String, date: Option[LocalDate] = None): List[String] = {
    val pathResolver = () => (
      s3PathDateInjector.tupled andThen
        s3PathVersionInjector(ReadOrWrite.read) andThen
        s3PathValidator)((path, date))

    mode match {
      case Environment.prod =>
        val pathResolution: PathResolution = pathResolver()
        logger.warn("missing s3 paths:" + pathResolution.invalidPathCount.toString)
        pathResolution.paths
      case Environment.local => handleLocalPath(path)
      case Environment.cci => handleLocalPath(path)
      case Environment.dev => pathResolver().paths
      case _ =>
        logger.warn("readpaths not configured for this environment, defaulting to no validation")
        s3PathDateInjector(path, date).paths
    }
  }

  def readPath(path: String, dates: Iterable[LocalDate]): List[String] = {
    val pathResolver: LocalDate => PathResolution =
      (date: LocalDate) => (
        s3PathDateInjector.tupled andThen
          s3PathVersionInjector(ReadOrWrite.read) andThen
          s3PathValidator)((path, Some(date)))

    mode match {
      case Environment.prod =>
        val pathResolution: PathResolution = dates.map(dt => pathResolver(dt)).reduce(_ + _)
        logger.warn("missing s3 paths:" + pathResolution.invalidPathCount.toString)
        pathResolution.paths
      case Environment.local => handleLocalPath(path)
      case Environment.cci => handleLocalPath(path)
      case Environment.dev => dates.map(dt => pathResolver(dt)).reduce(_ + _).paths
      case _ =>
        logger.warn("readpaths not configured for this environment, defaulting to no validation")
        dates.map(dt => s3PathDateInjector(path, Some(dt)).paths).reduce(_ ++ _)
    }
  }

  def writePath(path: String, date: Option[LocalDate]): List[String] = {
    val pathResolver = () => (s3PathDateInjector.tupled andThen s3PathVersionInjector(ReadOrWrite.write))(path, date)
    mode match {
      case Environment.local => List(List(System.getProperty("user.dir"), "src", "test", "resources").mkString("/") + path)
      case Environment.cci => List(List(System.getProperty("user.dir"), "src", "test", "resources").mkString("/") + path)
      case Environment.dev => pathResolver().paths
      case Environment.prod => pathResolver().paths
      case _ =>
        logger.warn("writepaths not configured for this environment, defaulting to test")
        s3PathDateInjector(path, date).paths
    }
  }
}

object FilePathResolver {
  def apply(mode: Environment.Value): FilePathResolver = new FilePathResolver(mode)

  def apply(mode: Environment.Value, version: Int): FilePathResolver = new FilePathResolver(mode, version = Some(version))

  def apply(mode: Environment.Value, version: Option[Int]): FilePathResolver = new FilePathResolver(mode, version = version)
}