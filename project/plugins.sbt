// Additional information on initialization
logLevel := Level.Warn

addSbtPlugin("com.twitter" %% "scrooge-sbt-plugin" % "22.1.0")

addSbtPlugin("com.github.sbt" % "sbt-release" % "1.1.0")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.10.0")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.10.0-RC1")

addSbtPlugin("ch.epfl.scala" % "sbt-version-policy" % "3.2.0")

// Patch the build tool graph without changing published library dependencies.
dependencyOverrides += "org.msgpack" % "msgpack-core" % "0.9.11"
