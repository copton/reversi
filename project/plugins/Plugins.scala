import sbt._
 
class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
	val akkaPlugin = "se.scalablesolutions.akka" % "akka-sbt-plugin" % "0.9.1"
	//val repo = "GH-pages repo" at "http://mpeltonen.github.com/maven/"
	val idea = "com.github.mpeltonen" % "sbt-idea-plugin" % "0.1-SNAPSHOT"
}
