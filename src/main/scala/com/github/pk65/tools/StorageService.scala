package com.github.pk65.tools

import zio.Task

trait StorageClient[A] {
  def handler: A
}

trait StorageService[A] {
  def open(credentials: Credentials): StorageClient[A]
  def close(client: StorageClient[A]): Task[Unit]
  def bucketExists(client: StorageClient[A], bucket: String): Task[Boolean]  
  def listObjects(client: StorageClient[A], bucket: String, prefix: String, filter: String => Boolean = _ => true): Task[Set[String]]
  def deleteObjects(client: StorageClient[A], bucket: String, prefix: String, files: List[String]): Task[(List[String], List[String])]
}
