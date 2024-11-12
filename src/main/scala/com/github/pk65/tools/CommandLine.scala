package com.github.pk65.tools

import scopt.OParser
case class Config(
  profile: Option[String],
  bucket: Option[String],
  prefix: Option[String],
  days: Option[Int],
  months: Option[Int],
  pattern: Option[String],
  dryRun: Boolean,
  help: Boolean
)

object CommandLine:
  def parse(args: List[String]): Either[String, Config] = {
    val builder = OParser.builder[Config]
    val parser1 = {
      import builder.*
      OParser.sequence(
        programName("scopt"),
        head("scopt", "4.1.0"),
        opt[String]('l',"profile")
          .action((x, c) => c.copy(profile = Some(x)))
          .text("profile name (required)"),
        opt[String]('b',"bucket")
          .action((x, c) => c.copy(bucket = Some(x)))
          .text("bucket name (required)"),
        opt[String]('p',"prefix")
          .action((x, c) => c.copy(prefix = Some(x)))
          .text("prefix folder"),
        opt[Int]('d',"days")
          .action((x, c) => c.copy(days = Some(x)))
          .text("last n days to preserve"),
        opt[Int]('m',"months")
          .action((x, c) => c.copy(months = Some(x)))
          .text("first n days of each month"),
        opt[String]('r',"pattern")
          .action((x, c) => c.copy(pattern = Some(x)))
          .text("file name pattern (required)"),
        opt[Unit]('h', "help")
          .action((_, c) => c.copy(help = true))
          .text("print help message and exit"),
        opt[Unit]('n', "dry-run")
          .action((_, c) => c.copy(dryRun = true))
          .text("dry run")
      )
    }
    OParser.parse(parser1, args, Config(profile = None, bucket = None, prefix = Some(""), days = Some(1), months = Some(1), pattern = None, dryRun = false, help = false)) match {
      case Some(config) =>
        if config.help then {
          Left(OParser.usage(parser1))
        } else {
          Right(config)
        }
      case None =>
        Left(OParser.usage(parser1))
    }
  }
