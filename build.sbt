name := """playfairy"""
organization := "com.playfairy"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.11"

//libraryDependencies += filters

//libraryDependencies += "commons-io" % "commons-io" % "2.4"

libraryDependencies ++= Seq(
  "com.google.code.gson" % "gson" % "2.8.0",
  "org.eclipse.jetty.osgi" % "jetty-osgi-alpn" % "9.4.1.v20170120",
  "io.netty" % "netty-tcnative-boringssl-static" % "1.1.33.Fork24",
  "org.mongodb" % "mongo-java-driver" % "2.12.2",
  "org.mongodb.morphia" % "morphia" % "0.108",
  "org.mongodb.morphia" % "morphia-logging-slf4j" % "0.108",
  "org.mongodb.morphia" % "morphia-validation" % "0.108",
  "commons-io" % "commons-io" % "2.4",
  filters,
  javaJdbc,
  cache,
  javaWs
)

resolvers ++= Seq(
  "Apache" at "http://repo1.maven.org/maven2/",
  "jBCrypt Repository" at "http://repo1.maven.org/maven2/org/",
  Resolver.url("Objectify Play Repository", url("http://schaloner.github.io/releases/"))(Resolver.ivyStylePatterns)
)