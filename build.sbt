import Dependencies._
import sbt.Keys._
import jp.pigumer.sbt.cloud.aws.cloudformation._

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging, AshScriptPlugin, DockerPlugin, CloudformationPlugin)
  .settings(
    name := "scala-cast",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.6",
    libraryDependencies ++= Seq(
      mp3spi,
      cast,
      akkaActor,
      akkaStream,
      awsS3,
      awsPolly,
      akkaStreamTestKit,
      scalaTest
    ),
    dockerBaseImage := "arm32v6/openjdk:8-jre-alpine",
    daemonUser in Docker := "root",
    mainClass in assembly := Some("jp.pigumer.cast.Cast")
  )
  .settings(
    awscfSettings := AwscfSettings(
      region = "ap-northeast-1"
    )
  )