package com.github.pk65.tools.aws

import zio.{ZIO, ZLayer}
import com.github.pk65.tools.{AppConfig, Config, Credentials}
import software.amazon.awssdk.services.s3.S3Client

case class ApplicationConfiguration (
  cfg: Config,
  credentials: Credentials,
  storageService: AwsStorageService
) extends AppConfig[S3Client]

object ApplicationConfiguration {
  val appLayer: ZLayer[Any, Nothing, AwsStorageService & AwsCredentialService] =
    AwsStorageService.layer ++ AwsCredentialService.layer

  def collect(cfg: Config): ZIO[AwsStorageService & AwsCredentialService, Throwable, ApplicationConfiguration] =
    for
      credSvc <- ZIO.service[AwsCredentialService]
      storageSvc <- ZIO.service[AwsStorageService]
      credentials <- credSvc.fromProfile(cfg.profile.getOrElse("default"))
      ac <- ZIO.succeed(ApplicationConfiguration(cfg, credentials, storageSvc))
    yield ac
}
