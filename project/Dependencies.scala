import sbt._

object Dependencies {
  lazy val akkaVersion = "2.5.3"

  lazy val cast = "su.litvak.chromecast" % "api-v2" % "0.10.2"
  lazy val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion

  lazy val awsSdkVersion = "1.11.271"
  lazy val awsPolly = "com.amazonaws" % "aws-java-sdk-polly" % awsSdkVersion
  lazy val awsS3 = "com.amazonaws" % "aws-java-sdk-s3" % awsSdkVersion

}