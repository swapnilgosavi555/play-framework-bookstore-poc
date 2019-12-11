name := """demo"""
organization := "knoldus"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.0"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "2.7.0"
libraryDependencies ++= Seq(
  "org.reactivemongo" %% "reactivemongo-play-json" % "0.12.3",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.18.0-play27"
)
// Adds additional packages into Twirl
//TwirlKeys.templateImports += "knoldus.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "knoldus.binders._"
