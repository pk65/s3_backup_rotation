# Backup rotation for S3

[![Scala CI](https://github.com/pk65/s3_backup_rotation/actions/workflows/scala.yml/badge.svg)](https://github.com/pk65/s3_backup_rotation/actions/workflows/scala.yml)

## Reason for the project

It is only for my personal use. I wanted to check how good [Cursor Tab](https://www.cursor.com) would be in helping me to learn [Scala ZIO library](https://zio.dev/).  
Also, I have a lot of databases and I need to rotate backups for each of them. So it is kind of useful.

## Usage

The pattern must match the whole name of the backup file.  
Date fragment of the pattern must represent a valid date so it can be parsed using java.time.format.DateTimeFormatter#ISO_LOCAL_DATE.  
The configuration requires endpoint url to be set. Regions are not supported yet.  
I have tested it only with my local [Minio](https://min.io/download?license=agpl&platform=linux) service and not with AWS S3.

## sbt

```sbt
run --dry-run --profile default --bucket service-backup --prefix backup.daily --days 3 --pattern service_db(?<date>\\d{4}-\\d{2}-\\d{2})\\.zip
```

## Universal packager

```shell
sbt "Universal / packageBin"
```

Unpack the package `target/universal/s3_backup_rotation-0.1.0-SNAPSHOT.zip` and run the app:

```shell
./s3_backup_rotation-0.1.0-SNAPSHOT/bin/s3_backup_rotation --dry-run --profile default --bucket service-backup --prefix backup.daily --days 3 --months 1 --pattern service_db(?<date>\\d{4}-\\d{2}-\\d{2})\\.zip
```
