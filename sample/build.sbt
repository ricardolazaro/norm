name := "sample"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)

unmanagedSourceDirectories in Compile += baseDirectory.value / "../src"

play.Project.playScalaSettings
