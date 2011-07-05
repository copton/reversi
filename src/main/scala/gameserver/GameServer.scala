package gameserver

import akka.actor._
import scala.collection.mutable.HashMap
import java.lang.Thread
import java.util.ArrayList
import tournament._
import tournament.plan._
import resourceadministrator.ResourceAdministrator
import messages._

class GameServer extends Actor{
	var resourceAdministrator: ActorRef = null
	var tournaments: HashMap[String, (ActorRef, Boolean)] = new HashMap
	var remoteNode: akka.remoteinterface.RemoteServerModule = null
	val remoteNodePort: Int = 9999

	override def preStart() {
		resourceAdministrator = createResourceAdministrator(10000)
		log.info("GameServer: resourceAdministrator started")
		remoteNode = Actor.remote.start("localhost", remoteNodePort)
		
	}


	def receive = {
		case ServerStart() =>
//			startFakeTournament(remoteNodePort, remoteNode, "fakeTournament1")
//			log.info("gameServer: fakeTournament2 started for testing purposes.")
//			startFakeTournament(remoteNodePort, remoteNode, "fakeTournament2")
//			log.info("gameServer: fakeTournament2 started for testing purposes.")

			prepareTestTournament(remoteNode)
			log.info("GameServer: testTournament1 started")
			prepareTestTournament(remoteNode)
			log.info("GameServer: testTournament2 started")



		case RequestPorts(amount: Int) =>
			log.info("got port request. forwarding...")
			resourceAdministrator.forward(RequestPorts(amount: Int))

		case ReleasePorts(portList: List[Int]) =>
			log.info("got ports to release. forwarding...")
			resourceAdministrator.forward(ReleasePorts(portList: List[Int]))
	
		case WebTest() =>
			self.reply("Webtest seems to work")

		case WebGetRoot() =>
			var reply = new _root_.messages.RootReply
			self.reply(reply)

		case WebLoadTournamentCollection() =>
			var result = new ArrayList[String]
			tournaments foreach ( (t1) => result.add(t1._1) )
			self.reply(result)

		case WebGetTournaments() =>
			val result = new _root_.messages.TournamentsReply(tournaments.size.toString)
			self.reply(result)

		case WebRequestActor(actorName: String) =>
//			self.reply(Actor.remote.actorFor(actorName, "localhost", remoteNodePort))
			self.reply((Actor.registry.actorsFor(actorName))(0))

		case WebPostForTournament(tournamentName: String, postArgument: String) =>
			val target: ActorRef = (Actor.registry.actorsFor("/tournaments/" + tournamentName))(0)

			postArgument match {
				case "start" =>
					target ! _root_.messages.Start()
				case "delete" =>
					target ! _root_.messages.Stop()
			}
			


		case _ => 
			println("unknown message received ")
			val reply: String = "<a href='http://akka.io'>Akka Actors rock!</a>"
			self.reply(reply)
			
	}

	def prepareTestTournament(remoteNode: akka.remoteinterface.RemoteServerModule): Unit = {
  		val plan = new DummyPlan
		val tournamentName = (resourceAdministrator !! _root_.messages.RequestTournamentName()).getOrElse(throw new RuntimeException("TIMEOUT")).asInstanceOf[String]
		val tournament = Actor.actorOf(new Tournament(plan, self, remoteNode, "/tournaments/"+tournamentName.toString()))
		log.info("GameServer: created a new tournament with the name " + tournamentName)
		tournaments += tournamentName.asInstanceOf[String] -> (tournament, false)
		tournament.start

	}
	
	def startFakeTournament(port: Int, testServer: akka.remoteinterface.RemoteServerModule, name: String): Unit = {
		println("startFaketournament entered")
		val fakeTournament = Actor.actorOf(new testStuff.TestFakeTournament(port, testServer, name))
		fakeTournament.start
		fakeTournament ! _root_.testStuff.TestStart()
	}

	def createResourceAdministrator(basePort: Int): ActorRef = {
		var resourceAdministrator = Actor.actorOf(new ResourceAdministrator(basePort))
		resourceAdministrator.setId("resourceAdministrator")
		resourceAdministrator.start
		return resourceAdministrator
	}


////////////////////// REST Connection Stuff //////////////////

	


////////////////////////////////////////////////////////////////


}


object RunGameServer {
	def main(args: Array[String]) {

		val gameServer = Actor.actorOf(new GameServer)
		gameServer.start

		val resourceAdmin = (Actor.registry.actorsFor("resourceAdministrator"))(0)
		val gamePort = (resourceAdmin !! _root_.messages.RequestPorts(1)).getOrElse(throw new RuntimeException("RunGameServer: TIMEOUT"))
		println("to test the resourceAdministrator: the port I requested is: " + (gamePort match {case l: List[Int] => l(0) }).toString  )
		
		var WebServer = new WebServerThread(gameServer)
		WebServer.start()
//		var webServer = new _root_.ch.ethz.inf.vs.projectname.JerseyMain()
//		webServer.myServerStarter(gameServer)

		gameServer ! _root_.messages.ServerStart()
	}
}

class WebServerThread(var gameServer: ActorRef) extends Thread {

	var webServer = new _root_.ch.ethz.inf.vs.projectname.JerseyMain()

	override def run() = webServer.myServerStarter(gameServer)
}


 
