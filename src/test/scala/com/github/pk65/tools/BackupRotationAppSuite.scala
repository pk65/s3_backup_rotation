package com.github.pk65.tools

import zio.test.{ZIOSpecDefault, assertTrue}

case class DummyApplicationConfiguration(cfg: Config, credentials: Credentials, storageService: DummyStorageService) extends AppConfig[DummyClient]

object BackupRotationAppSuite extends ZIOSpecDefault {
  val credSvc = new DummyCredentialService
  val keysList = Set(
    "daily/service_db2024-01-01.zip", "daily/service_db2024-01-02.zip", "daily/service_db2024-01-03.zip",
    "daily/service_db2024-02-01.zip", "daily/service_db2024-02-02.zip", "daily/service_db2024-02-03.zip",
    "daily/service_db2024-03-01.zip", "daily/service_db2024-03-02.zip", "daily/service_db2024-03-03.zip.gpg",
    "daily/service_db2024-04-07.zip", "daily/service_db2024-04-08.zip", "daily/service_db2024-04-09.zip",
    "daily/service_db2024-04-11.zip", "daily/service_db2024-04-12.zip", "daily/service_db2024-04-13.zip.gpg"
  )
  val storageSvc = new DummyStorageService(keysList)
  val expected = List(
    "service_db2024-01-02.zip", "service_db2024-01-03.zip",
    "service_db2024-02-02.zip", "service_db2024-02-03.zip",
    "service_db2024-03-02.zip", "service_db2024-04-08.zip"
  )
  val expectedTwoDaysOfMonth = List(
    "service_db2024-01-03.zip",
    "service_db2024-02-03.zip",
  )
  val expectedZeroDays = List(
    "service_db2024-01-01.zip", "service_db2024-01-02.zip", "service_db2024-01-03.zip",
    "service_db2024-02-01.zip", "service_db2024-02-02.zip", "service_db2024-02-03.zip",
    "service_db2024-03-01.zip", "service_db2024-03-02.zip",
    "service_db2024-04-07.zip", "service_db2024-04-08.zip"
  )
  val expectedClear = List(
    "service_db2024-01-01.zip", "service_db2024-01-02.zip", "service_db2024-01-03.zip",
    "service_db2024-02-01.zip", "service_db2024-02-02.zip", "service_db2024-02-03.zip",
    "service_db2024-03-01.zip", "service_db2024-03-02.zip",
    "service_db2024-04-07.zip", "service_db2024-04-08.zip", "service_db2024-04-09.zip",
    "service_db2024-04-11.zip", "service_db2024-04-12.zip"
  )
  val cfg = Config(Some("dev-profile"), Some("dev-bucket"), Some("daily"), Some(3), Some(1), Some("""^service_db(?<date>\d{4}-\d{2}-\d{2})\.zip$"""), false, false)
  val cfgTwoDays = cfg.copy(months = Some(2))
  val cfgZeroDays = cfg.copy(months = Some(0))
  val cfgClear = cfg.copy(days = Some(0), months = Some(0))
  override def spec = suite("BackupRotationApp")(
    test("preserve first day of each month") {
      for
        creds <- credSvc.fromProfile("default")
        res <- StorageManager.run(DummyApplicationConfiguration(cfg, creds, storageSvc))
      yield assertTrue(res._1 == expected && res._2 == List.empty)
    },
    test("preserve first 2 days of each month") {
      for
        creds <- credSvc.fromProfile("default")
        res <- StorageManager.run(DummyApplicationConfiguration(cfgTwoDays, creds, storageSvc))
      yield assertTrue(res._1 == expectedTwoDaysOfMonth && res._2 == List.empty)
    },
    test("do not preserve any first days of each month") {
      for
        creds <- credSvc.fromProfile("default")
        res <- StorageManager.run(DummyApplicationConfiguration(cfgZeroDays, creds, storageSvc))
      yield assertTrue(res._1 == expectedZeroDays && res._2 == List.empty)
    },
    test("delete everything") {
      for
        creds <- credSvc.fromProfile("default")
        res <- StorageManager.run(DummyApplicationConfiguration(cfgClear, creds, storageSvc))
      yield assertTrue(res._1 == expectedClear && res._2 == List.empty)
    }
  )
}
