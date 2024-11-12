package com.github.pk65.tools

// For more information on writing tests, see
// https://scalameta.org/munit/docs/getting-started.html
class StorageManagerSuite extends munit.FunSuite {
  test("selection of files based on rules") {
    val config = Config(None, None, None, Some(2), Some(1), Some("""^service_db(?<date>\d{4}-\d{2}-\d{2})\.zip$"""), false, false)
    val keysList = Set(
      "service_db2024-01-01.zip", "service_db2024-01-02.zip", "service_db2024-01-03.zip",
      "service_db2024-02-01.zip", "service_db2024-02-02.zip", "service_db2024-02-03.zip",
      "service_db2024-03-01.zip", "service_db2024-03-02.zip", "service_db2024-03-03.zip.gpg",
      "service_db2024-04-07.zip","service_db2024-04-08.zip", "service_db2024-04-09.zip",
      "service_db2024-04-11.zip","service_db2024-04-12.zip", "service_db2024-04-13.zip.gpg"
    )
    val expected = List(
      "service_db2024-01-02.zip",
      "service_db2024-01-03.zip",
      "service_db2024-02-02.zip",
      "service_db2024-02-03.zip",
      "service_db2024-03-02.zip",
      "service_db2024-04-08.zip",
      "service_db2024-04-09.zip",
    )
    assertEquals(StorageManager.filterBasedOnRules(config, keysList).sorted, expected)
  }

  test("selection of encrypted backup files based on rules") {
    val config = Config(None, None, None, Some(3), Some(1), Some("""^service_db(?<date>\d{4}-\d{2}-\d{2})\.zip\.gpg$"""), false, false)
    val keysList = Set(
      "service_db2024-01-01.zip.gpg", "service_db2024-01-02.zip.gpg", "service_db2024-01-03.zip.gpg",
      "service_db2024-02-01.zip.gpg", "service_db2024-02-02.zip.gpg", "service_db2024-02-03.zip.gpg",
      "service_db2024-03-01.zip.gpg", "service_db2024-03-02.zip.gpg", "service_db2024-03-03.zip.gpg",
      "service_db2024-04-07.zip.gpg","service_db2024-04-08.zip.gpg", "service_db2024-04-09.zip.gpg",
      "service_db2024-04-11.zip.gpg","service_db2024-04-12.zip.gpg", "service_db2024-04-13.zip"
    )
    val expected = List(
      "service_db2024-01-02.zip.gpg",
      "service_db2024-01-03.zip.gpg",
      "service_db2024-02-02.zip.gpg",
      "service_db2024-02-03.zip.gpg",
      "service_db2024-03-02.zip.gpg",
      "service_db2024-03-03.zip.gpg",
      "service_db2024-04-08.zip.gpg",
    )
    assertEquals(StorageManager.filterBasedOnRules(config, keysList).sorted, expected)
  }
}
