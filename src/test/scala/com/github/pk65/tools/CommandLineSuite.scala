package com.github.pk65.tools

class CommandLineSuite extends munit.FunSuite {
  test("parse command line arguments") {
    val args = List("--profile", "dev-profile", "--bucket", "test-bucket", "--prefix", "test-prefix", "--days", "3", "--pattern", "^service_db(?<date>\\d{4}-\\d{2}-\\d{2})\\.zip$")
    val config = CommandLine.parse(args)
    config match {
      case Left(msg) => fail(msg)
      case Right(cfg) =>
        assertEquals(cfg.profile, Some("dev-profile"))
        assertEquals(cfg.bucket, Some("test-bucket"))
        assertEquals(cfg.prefix, Some("test-prefix"))
        assertEquals(cfg.days, Some(3))
        assertEquals(cfg.pattern, Some("""^service_db(?<date>\d{4}-\d{2}-\d{2})\.zip$"""))
        assertEquals(cfg.dryRun, false)
        assertEquals(cfg.help, false)
    }
  }
}
