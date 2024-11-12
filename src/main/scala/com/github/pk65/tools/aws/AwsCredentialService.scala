package com.github.pk65.tools.aws

import scala.io.Source
import com.github.pk65.tools.{CredentialService, Credentials, IniFileReader}
import zio.{ZIO, ZLayer}

class AwsCredentialService extends CredentialService {
  override def fromProfile(profile: String): ZIO[Any, Throwable, Credentials] =
    // TODO: handle exceptions
    val buffer = Source.fromFile(s"${System.getProperty("user.home")}/.aws/credentials").mkString
    val creds = IniFileReader.read(buffer.split("\n"))(profile)
    ZIO.succeed(Credentials(creds("endpoint_url"), creds("aws_access_key_id"), creds("aws_secret_access_key")))
}

object AwsCredentialService {
  val layer: ZLayer[Any, Nothing, AwsCredentialService] = ZLayer.succeed(new AwsCredentialService)
}
