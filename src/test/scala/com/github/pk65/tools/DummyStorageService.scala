package com.github.pk65.tools

import zio.{Task, ZIO}

case class DummyClient()
case class DummyStorageClient() extends StorageClient[DummyClient] {
  val dc = new DummyClient
  override def handler: DummyClient = dc
}

class DummyStorageService(keysList: Set[String]) extends StorageService[DummyClient] {
  override def bucketExists(client: StorageClient[DummyClient], bucket: String): Task[Boolean] =
    ZIO.succeed(true)

  override def listObjects(client: StorageClient[DummyClient], bucket: String, prefix: String, filter: String => Boolean = _ => true): Task[Set[String]] =
    ZIO.succeed(keysList.filter(filter))

  override def deleteObjects(client: StorageClient[DummyClient], bucket: String, prefix: String, files: List[String]): Task[(List[String], List[String])] =
    ZIO.succeed((files, List.empty))

  override def close(client: StorageClient[DummyClient]): Task[Unit] =
    ZIO.succeed(())

  override def open(credentials: Credentials): StorageClient[DummyClient] =
    new DummyStorageClient
}
