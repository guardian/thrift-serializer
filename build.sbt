import sbtrelease._
import ReleaseStateTransformations._
import sbtversionpolicy.withsbtrelease.ReleaseVersion

name := "thrift-serializer"
organization := "com.gu"
scalaVersion := "2.13.12"

libraryDependencies ++= Seq(
    "com.twitter" %% "scrooge-core" % "22.1.0",
    "org.apache.thrift" % "libthrift" % "0.17.0",
  // this has optimised native binaries for all platforms, so is only worth for long lived apps
    "com.github.luben" % "zstd-jni" % "1.4.9-1",
    "org.scalatest" %% "scalatest" % "3.0.8" % "test",
    "com.gu" %% "content-api-models-scala" % "17.3.0" % "test"
)

// Settings for building the thrift definition used in test
Test / scroogeThriftSourceFolder := { baseDirectory {
    base => base / "src/test/thrift"
}.value }
Test / scroogeThriftOutputFolder := (Test / sourceManaged).value
Test / managedSourceDirectories += (Test / scroogeThriftOutputFolder).value

scalacOptions := Seq("-deprecation", "-release:11")
javacOptions ++= Seq("-target", "11", "-source", "11")

description := "Serialize thrift models into bytes"

licenses := Seq(License.Apache2)

crossScalaVersions := Seq("2.12.18", "2.13.12")

releaseCrossBuild := true
releaseVersion := ReleaseVersion.fromAggregatedAssessedCompatibilityWithLatestRelease().value
releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    setNextVersion,
    commitNextVersion,
)

Test / testOptions +=
  Tests.Argument(TestFrameworks.ScalaTest, "-u", s"test-results/scala-${scalaVersion.value}", "-o")