import sbtrelease._
import ReleaseStateTransformations._

name := "thrift-serializer"
organization := "com.gu"
scalaVersion := "2.13.9"

credentials += Credentials(Path.userHome / ".sbt" / "sonatype_credentials")
ThisBuild / versionScheme := Some("semver-spec")

libraryDependencies ++= Seq(
    "com.twitter" %% "scrooge-core" % "22.1.0",
    "org.apache.thrift" % "libthrift" % "0.17.0",
  // this has optimised native binaries for all platforms, so is only worth for long lived apps
    "com.github.luben" % "zstd-jni" % "1.3.5-2",
    "org.scalatest" %% "scalatest" % "3.0.8" % "test"
)

// Settings for building the thrift definition used in test
Test / scroogeThriftSourceFolder := { baseDirectory {
    base => base / "src/test/thrift"
}.value }
Test / scroogeThriftOutputFolder := (Test / sourceManaged).value
Test / managedSourceDirectories += (Test / scroogeThriftOutputFolder).value

// Publish settings
scmInfo := Some(ScmInfo(url("https://github.com/guardian/thrift-serializer"),
    "scm:git:git@github.com:guardian/thrift-serializer.git"))

description := "Serialize thrift models into bytes"

pomExtra := (
    <url>https://github.com/guardian/thrift-serializer</url>
    <developers>
        <developer>
            <id>Reettaphant</id>
            <name>Reetta Vaahtoranta</name>
            <url>https://github.com/guardian</url>
        </developer>
    </developers>
    )

licenses := Seq("Apache V2" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))

crossScalaVersions := Seq("2.12.17", "2.13.9")
publishTo := Some(
    if (isSnapshot.value)
        Opts.resolver.sonatypeSnapshots
    else
        Opts.resolver.sonatypeStaging
)

releaseCrossBuild := true
releasePublishArtifactsAction := PgpKeys.publishSigned.value
releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    publishArtifacts,
    setNextVersion,
    commitNextVersion,
    releaseStepCommand("sonatypeRelease"),
    pushChanges
)
