name := "First"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.1"

resourceDirectory in (Compile, assembly) := baseDirectory.value / "resources"

val workaround = {
  sys.props += "packaging.type" -> "jar"
  ()
}

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.3.3"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.3.3"

libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.3.3" % "provided"
