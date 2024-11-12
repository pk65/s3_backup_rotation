package com.github.pk65.tools

import zio.Task

case class Credentials(endpoint: String, accessKey: String, secretKey: String)

trait CredentialService {
  def fromProfile(profile: String): Task[Credentials]
}
