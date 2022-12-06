ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "optics"
  )

libraryDependencies ++= Seq(
  "dev.optics" %% "monocle-core" % "3.1.0",
  "dev.optics" %% "monocle-macro" % "3.1.0",
)

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-Ymacro-annotations",
  "-language:higherKinds"
)