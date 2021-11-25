enablePlugins(ScalaJSPlugin)

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.1.0"
libraryDependencies += "org.scala-lang.modules" %% "scala-async" % "0.10.0"
libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided

scalacOptions += "-Xasync"

name := "helsinki-tango"
scalaVersion := "2.13.6"

// This is an application with a main method
scalaJSUseMainModuleInitializer := true
