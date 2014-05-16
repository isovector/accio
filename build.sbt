name := "Accio"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "com.typesafe.play" %% "play-slick" % "0.6.0.1",
  "org.xerial" % "sqlite-jdbc" % "3.7.2"
)     

play.Project.playScalaSettings
