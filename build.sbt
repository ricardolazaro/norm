name := "norm"

version := "1.0.0-SNAPSHOT"


resolvers ++= Seq(
  Resolver.url("Objectify Play Repository", url("http://schaloner.github.io/releases/"))(Resolver.ivyStylePatterns),
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies += "play" % "anorm_2.9.3" % "2.1.1"

libraryDependencies += "play" %% "play-java-jdbc" % "2.1.5"

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.10.3"

libraryDependencies += "org.specs2" %% "specs2" % "2.3.12" % "test"

libraryDependencies += "play" %% "play-test" % "2.1.5" % "test"

