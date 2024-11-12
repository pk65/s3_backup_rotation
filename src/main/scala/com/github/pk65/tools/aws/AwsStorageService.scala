package com.github.pk65.tools.aws

import com.github.pk65.tools.{Credentials, StorageClient, StorageService}
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.HeadBucketRequest
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request
import zio.{ZIO, ZLayer}

import java.net.URI
import scala.annotation.tailrec
import scala.jdk.CollectionConverters.*
import scala.util.{Failure, Success, Try}
case class AwsStorageClient(s3: S3Client) extends StorageClient[S3Client] {
  override def handler: S3Client = s3
}

class AwsStorageService extends StorageService[S3Client] {

  override def open(credentials: Credentials): StorageClient[S3Client] =
    AwsStorageClient(S3Client
      .builder()
      .endpointOverride(URI.create(credentials.endpoint))
      .forcePathStyle(true)
      .region(Region.of("NONE"))
      .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(credentials.accessKey, credentials.secretKey)))
      .build())

  override def close(client: StorageClient[S3Client]): ZIO[Any, Throwable, Unit] =
    ZIO.attempt(client.handler.close())

  override def bucketExists(client: StorageClient[S3Client], bucket: String): ZIO[Any, Throwable, Boolean] =
    ZIO.attempt {
      Try {
        client.handler.headBucket(HeadBucketRequest.builder().bucket(bucket).build())
      } match {
        case Success(_) => true
        case Failure(_) => false
      }
    }

  override def listObjects(client: StorageClient[S3Client], bucket: String, prefix: String, filter: String => Boolean = _ => true): ZIO[Any, Throwable, Set[String]] =
    ZIO.attempt {
      val requestBuilder = ListObjectsV2Request.builder().bucket(bucket).prefix(prefix).maxKeys(100)
      getScalaSeq(client.handler, requestBuilder, filter).toSet
    }
  
  override def deleteObjects(client: StorageClient[S3Client], bucket: String, prefix: String, files: List[String]): ZIO[Any, Throwable, (List[String], List[String])] =
    ZIO.attempt {
      val allAttempts =files.map(f =>
        val res = client.handler.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(s"$prefix/$f").build())
        res.sdkHttpResponse().isSuccessful match
          case true => f -> true
          case false => f -> false
      )
      (allAttempts.filter(_._2).map(_._1), allAttempts.filterNot(_._2).map(_._1))
    }

  @tailrec
  private def getScalaSeq(client: S3Client, requestBuilder: ListObjectsV2Request.Builder, filter: String => Boolean = _ => true, result: Seq[String] = Seq.empty): Seq[String] = {
    val resp = client.listObjectsV2(requestBuilder.build())
    val current = resp.contents().asScala.toSeq.map(o => o.key()).filter(filter)
    val next = resp.nextContinuationToken()
    if next == null then result ++ current
    else getScalaSeq(client, requestBuilder.continuationToken(next), filter, result ++ current)
  }
}

object AwsStorageService {
  val layer: ZLayer[Any, Nothing, AwsStorageService] = ZLayer.succeed(new AwsStorageService)
}
