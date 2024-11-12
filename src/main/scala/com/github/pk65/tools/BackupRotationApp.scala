package com.github.pk65.tools

import scala.util.Try
import zio.{Chunk, ExitCode, RIO, ZIO, ZIOAppArgs, ZIOAppDefault}
import com.github.pk65.tools.aws.ApplicationConfiguration as AC


object BackupRotationApp extends ZIOAppDefault {

  def defaultArgumentsIfNone(args: Chunk[String]): List[String] =
    if args.isEmpty then
      Try {
        sys.env("APP_RUN_ARGS") match
          case "" => List.empty
          case s => s.split("""\s+""").toList
      }.getOrElse(List.empty)
    else args.toList

  val blueprint =
    for
      zioArgs <- ZIOAppArgs.getArgs
      cfgEither <- ZIO.attempt(CommandLine.parse(defaultArgumentsIfNone(zioArgs)))
      cfg <- cfgEither match {
        case Left(msg) => ZIO.fail(msg)
        case Right(cfg) => ZIO.succeed(cfg)
      }
      ac <- AC.collect(cfg).provide(AC.appLayer)
      result <- StorageManager.run(ac)
    yield result

  override def run: RIO[ZIOAppArgs, ExitCode] = blueprint.foldZIO(
    failure =>
      ZIO.logError(s"FAILURE = $failure\n") *> ZIO.succeed(ExitCode.failure),
    success => ZIO.log(s"SUCCESS = $success\n") *> ZIO.succeed(ExitCode.success)
  )

}
