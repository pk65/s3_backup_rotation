ThisBuild / organization := "com.github.pk65"
Compile / run / fork := true
Universal / packageBin / mappings ++= Seq(
  file("README.md") -> "README.md",
  file("LICENSE.txt") -> "LICENSE.txt"
)

val scala3Version = "3.5.2"
val LogbackVersion = "1.5.12"
val ZioVersion = "2.1.12"

lazy val root = project
  .in(file("."))
  .settings(
    name := "s3_backup_rotation",
    version := "0.1.0-SNAPSHOT",
    maintainer := "pawel.kuszynski@gmail.com",
    scalaVersion := scala3Version,
    scalacOptions ++= {
      Seq(
        "-new-syntax",
        "-explain-types",
        "-rewrite",
        "-source:future-migration",
        "-Yexplicit-nulls",
        "-Wunused:imports"
      )
    },
    scalacOptions --= {
      Seq("-Xfatal-warnings")
    },
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % ZioVersion,
      "dev.zio" %% "zio-test" % ZioVersion % Test,
      "dev.zio" %% "zio-test-sbt" % ZioVersion % Test,
      "dev.zio" %% "zio-test-magnolia" % ZioVersion % Test,
      "software.amazon.awssdk" % "s3" % "2.29.9",
      "com.github.scopt" %% "scopt" % "4.1.0",
      "org.scalameta" %% "munit" % "1.0.2" % Test,
      "ch.qos.logback" % "logback-classic" % LogbackVersion
    )
  )
enablePlugins(JavaAppPackaging)
enablePlugins(LauncherJarPlugin)
