import sbt._

class MainProject(info: ProjectInfo) extends DefaultProject(info) with AkkaProject {

  //Repositories---

  val scala_tools_org = "scala-tools.org" at "http://scala-tools.org/repo-releases"
  val scala_tools_org_snapshots = "scala-tools.org.snapshots" at "http://scala-tools.org/repo-snapshots"
  val akka_repo = "Akka Maven Repository" at "http://www.scalablesolutions.se/akka/repository"
  val scalaToolsSnapshots = "Scala-Tools Maven Repository" at "http://nexus.scala-tools.org/content/repositories/snapshots/"

  //---Repositories

  //Dependencies---

  val junit = "junit"  % "junit"  % "4.7"  % "test"
  val specs = "org.scala-tools.testing"  % "specs_2.8.0"  % "1.6.6-SNAPSHOT"  % "test"
  val scalatest = "org.scalatest" % "scalatest" % "1.2" % "test"

  // Akka
  val akkaRemote = akkaModule("remote")
  /** 
  val akkaAMQP = akkaModule("amqp")
  val akkaCamel = akkaModule("camel")
  val akkaHttp = akkaModule("http")
  val akkaJTA = akkaModule("jta")
  val akkaKernel = akkaModule("kernel")
  val akkaCassandra = akkaModule("persistence-cassandra")
  val akkaMongo = akkaModule("persistence-mongo")
  val akkaRedis = akkaModule("persistence-redis")
  val akkaSpring = akkaModule("spring")
  */

  //---Dependencies

}
