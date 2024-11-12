package com.github.pk65.tools

trait AppConfig[A] {
  def cfg: Config
  def credentials: Credentials
  def storageService: StorageService[A]
}
