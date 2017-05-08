name := """playfairy"""
organization := "com.playfairy"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.11"

libraryDependencies += filters

libraryDependencies += "commons-io" % "commons-io" % "2.4"