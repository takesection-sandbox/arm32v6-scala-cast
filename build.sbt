import Dependencies._
import sbt.Keys._

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging, AshScriptPlugin, DockerPlugin)
  .settings(
    name := "scala-cast",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.4",
    libraryDependencies ++= Seq(
      mp3spi,
      cast,
      akkaActor,
      akkaStream,
      awsPolly,
      akkaStreamTestKit,
      scalaTest
    ),
    dockerBaseImage := "arm32v6/openjdk:8-jre-alpine",
    daemonUser in Docker := "root",
    mainClass in assembly := Some("jp.pigumer.cast.Cast")
  )