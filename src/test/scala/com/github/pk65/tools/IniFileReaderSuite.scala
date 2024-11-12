package com.github.pk65.tools

class IniFileReaderSuite extends munit.FunSuite {
  test("read aws credentials") {
    val content = """
      |[dev-profile]
      |endpoint_url = http://localhost:9000
      |aws_access_key_id = admin
      |aws_secret_access_key = password
      |
      |[other-profile]
      |endpoint_url = http://localhost:9001
      |aws_access_key_id = other
      |aws_secret_access_key = other
      |
      |[test-profile]
      |aws_access_key_id = test
      |aws_secret_access_key = test
      |
      |[empty-profile]
      |
      |[invalid-profile]
      |invalid = invalid
      """.stripMargin
    val creds = IniFileReader.read(content.split("\n"))
    assertEquals(creds("dev-profile")("endpoint_url"), "http://localhost:9000")
    assertEquals(creds("dev-profile")("aws_access_key_id"), "admin")
    assertEquals(creds("dev-profile")("aws_secret_access_key"), "password")
    assertEquals(creds("other-profile")("endpoint_url"), "http://localhost:9001")
    assertEquals(creds("other-profile")("aws_access_key_id"), "other")
    assertEquals(creds("other-profile")("aws_secret_access_key"), "other")
    assertEquals(creds("test-profile")("aws_access_key_id"), "test")
    assertEquals(creds("test-profile")("aws_secret_access_key"), "test")
    assertEquals(creds("test-profile").contains("endpoint_url"), false)
    assertEquals(creds.contains("empty-profile"), false)
    assertEquals(creds.contains("invalid-profile"), true)
    assertEquals(creds("invalid-profile")("invalid"), "invalid")
  }
}
