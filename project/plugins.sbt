
resolvers ++= Seq(
    Resolver.url("sbt-plugin-snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns)
    )

addSbtPlugin("com.twitter" %% "scrooge-sbt-plugin" % "3.16.3")

