import sbtrelease._
import ReleaseStateTransformations._

name := "thrift-serializer"
organization := "com.gu"
scalaVersion := "2.11.7"

import com.twitter.scrooge._

seq(ScroogeSBT.newSettings: _*)


ScroogeSBT.scroogeThriftOutputFolder in Compile := sourceManaged.value / "thrift"

libraryDependencies ++= Seq(
    "com.twitter" %% "scrooge-core" % "3.17.0",
    "com.gu" %% "auditing-thrift-model" % "0.0.1",
    "org.apache.thrift" % "libthrift" % "0.9.2",
    "org.scalatest" %% "scalatest" % "2.2.6" % "test",
    "org.scalatestplus" %% "play" % "1.4.0-M4" % "test"
)

unmanagedResourceDirectories in Compile += baseDirectory.value / "src/main"

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
    releaseStepCommand("sonatypeReleaseAll"),
    pushChanges
)
