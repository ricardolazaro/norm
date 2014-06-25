import play.PlayScala

name := "sample"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"


libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)

libraryDependencies += "org.postgresql" % "postgresql" % "9.3-1100-jdbc41"

unmanagedSourceDirectories in Compile += baseDirectory.value / "../src"
