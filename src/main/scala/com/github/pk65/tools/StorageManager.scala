package com.github.pk65.tools

import zio.{Task, ZIO}

import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Try

object StorageManager {
  def run[A](ac: AppConfig[A]): Task[(List[String], List[String])] =
    storageResource(ac.cfg, ac.credentials, ac.storageService) { cli =>
      useResource(ac.cfg, cli, ac.storageService)
    }

  def storageResource[A](cfg: Config, credentials: Credentials, provider: StorageService[A]): ZIO.Release[Any, Throwable, StorageClient[A]] =
    ZIO.acquireReleaseWith(ZIO.succeed(provider.open(credentials)))
                          (client => ZIO.succeed(provider.close(client)))

  def useResource[A](cfg: Config, cli: StorageClient[A], provider: StorageService[A]): Task[(List[String], List[String])] =
    if cfg.help then ZIO.succeed((List("Help ----"), List.empty))
    else if cfg.profile.nonEmpty && cfg.bucket.nonEmpty
    then 
      for 
        toBeDeleted <- selectFiles(cfg, cli, provider)
        (reallyDeleted, failed) <- if cfg.dryRun then ZIO.succeed((toBeDeleted, List.empty))
         else provider.deleteObjects(cli, cfg.bucket.get, cfg.prefix.get, toBeDeleted)
      yield (reallyDeleted, failed)
    else ZIO.fail(new IOException("Usage error"))

  def selectFiles[A](config: Config, cli: StorageClient[A], provider: StorageService[A]): Task[List[String]] =
    provider.bucketExists(cli, config.bucket.get).flatMap {
      case true => for
          files <- provider.listObjects(cli, config.bucket.get, config.prefix.get, filterStorageObjects(config.prefix.get, config.pattern.get))
          withoutPrefix = files.map(o => removePrefix(config.prefix.get, o))
          filtered = filterBasedOnRules(config, withoutPrefix.toSet)
        yield filtered
      case false => ZIO.fail(new IOException(s"Bucket ${config.bucket.get} does not exist"))
    }

  def filterBasedOnRules(config: Config, keysList: Set[String]): List[String] =
    val predKeyList = keysList.filter(fileNamePredicate(_, config.pattern.get))
    val toBeExcluded = getFirstFilesFromEachMonth(predKeyList, config.pattern.get, config.months.getOrElse(1))
      ++ predKeyList.toList.sorted.takeRight(config.days.getOrElse(1)).toSet
    predKeyList.filterNot(toBeExcluded.contains).toList.sorted

  private def filterStorageObjects(prefix: String, pattern: String): String => Boolean =
    key => key.contains(prefix) && fileNamePredicate(removePrefix(prefix, key), pattern)

  private def getDate(s: String, pattern: String): Option[LocalDate] =
    val p = pattern.r
    if p.matches(s) then Try(LocalDate.parse(p.findFirstMatchIn(s).get.group("date"))).toOption
    else None

  private def fileNamePredicate(s: String, pattern: String): Boolean =
    pattern.r.matches(s)

  private def getFirstFilesFromEachMonth(keysList: Set[String], pattern: String, months: Int): Set[String] =
    keysList.filter(fileNamePredicate(_, pattern))
      .groupBy(getDate(_, pattern).map(_.format(DateTimeFormatter.ofPattern("yyyy-MM"))))
      .filter(e => e._1.isDefined)
      .map(s => s._2.toList.sorted.take(months)).flatten.toSet

  private def removePrefix(prefix: String, key: String): String =
    key.stripPrefix(prefix).stripPrefix("/")
}
