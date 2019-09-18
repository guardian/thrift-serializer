import sbtrelease._
import ReleaseStateTransformations._

name := "thrift-serializer"
organization := "com.gu"
scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
    "com.twitter" %% "scrooge-core" % "19.9.0",
    "org.apache.thrift" % "libthrift" % "0.12.0",
    "com.github.luben" % "zstd-jni" % "1.3.5-2",
    "org.scalatest" %% "scalatest" % "3.0.8" % "test"
)

// Settings for building the thrift definition used in test
scroogeThriftSourceFolder in Test <<= baseDirectory {
    base => base / "src/test/thrift"
}
scroogeThriftOutputFolder in Test := (sourceManaged in Test).value
managedSourceDirectories in Test += (scroogeThriftOutputFolder in Test).value

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

crossScalaVersions := Seq("2.11.12", "2.12.10", "2.13.1")

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
