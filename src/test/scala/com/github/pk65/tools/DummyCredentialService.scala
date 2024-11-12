package com.github.pk65.tools

import zio.{Task, ZIO}

class DummyCredentialService extends CredentialService {
  override def fromProfile(profile: String): Task[Credentials] =
    ZIO.succeed(Credentials("http://localhost:9000", "minioadmin", "miniopass"))
}
