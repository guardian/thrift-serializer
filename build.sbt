import sbtrelease._
import ReleaseStateTransformations._
import sbtversionpolicy.withsbtrelease.ReleaseVersion

name := "thrift-serializer"
organization := "com.gu"
scalaVersion := "2.13.12"

ThisBuild / versionScheme := Some("semver-spec")

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

scalacOptions++= Seq("-unchecked", "-release:11")

// Publish settings
//scmInfo := Some(ScmInfo(url("https://github.com/guardian/thrift-serializer"),
//    "scm:git:git@github.com:guardian/thrift-serializer.git"))

description := "Serialize thrift models into bytes"

//pomExtra := (
//    <url>https://github.com/guardian/thrift-serializer</url>
//    <developers>
//        <developer>
//            <id>Reettaphant</id>
//            <name>Reetta Vaahtoranta</name>
//            <url>https://github.com/guardian</url>
//        </developer>
//    </developers>
//    )

licenses := Seq(License.Apache2)

crossScalaVersions := Seq("2.12.18", "2.13.12")

//publishTo := Some(
//    if (isSnapshot.value)
//        Opts.resolver.sonatypeSnapshots
//    else
//        Opts.resolver.sonatypeStaging
//)

releaseCrossBuild := true
//releasePublishArtifactsAction := PgpKeys.publishSigned.value
releaseVersion := ReleaseVersion.fromAggregatedAssessedCompatibilityWithLatestRelease().value
releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    //publishArtifacts,
    setNextVersion,
    commitNextVersion,
    //releaseStepCommand("sonatypeRelease"),
    //pushChanges
)

Test / testOptions +=
  Tests.Argument(TestFrameworks.ScalaTest, "-u", s"test-results/scala-${scalaVersion.value}", "-o")