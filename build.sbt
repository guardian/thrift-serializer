name := "thrift-serializer"

version := "0.0.1-SNAPSHOT"

organization := "com.gu"

com.twitter.scrooge.ScroogeSBT.newSettings

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
    "org.apache.thrift" % "libthrift" % "0.9.2",
    "com.twitter" %% "scrooge-core" % "3.17.0"
)
