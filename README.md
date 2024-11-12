# Reason for the project

It is only for my personal use. I wanted to check how good Cursor Tab would be in helping me to learn Scala ZIO library.
Also, I have a lot of databases and I need to rotate backups for each of them.

## Usage

The pattern must match the whole name of the backup file.
Date fragment of the pattern must represent a valid date so it can be parsed using java.time.format.DateTimeFormatter#ISO_LOCAL_DATE.
The configuration requires endpoint url to be set. Regions are not supported yet.
I have tested it only with my local Minio service and not with AWS S3.

## sbt

```sbt
run --dry-run --profile default --bucket service-backup --prefix backup.daily --days 3 --pattern service_db(?<date>\\d{4}-\\d{2}-\\d{2})\\.zip
```

## Universal packager

```shell
sbt universal:packageBin
```

Unpack the package and run the app:

```shell
./s3_backup_rotation-0.1.0-SNAPSHOT/bin/s3_backup_rotation.bat --dry-run --profile default --bucket service-backup --prefix backup.daily --days 3 --months 1 --pattern service_db(?<date>\\d{4}-\\d{2}-\\d{2})\\.zip
```
