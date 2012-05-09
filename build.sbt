import AssemblyKeys._ // put this at the top of the file

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "com.typesafe.akka" % "akka-actor" % "2.0"

libraryDependencies += "org.apache.commons" % "commons-compress" % "1.4"

assemblySettings

test in assembly := {}

seq(ProguardPlugin.proguardSettings :_*)

proguardOptions += keepMain("tw.epubcoverter.Converter")

proguardOptions += keepAllScala