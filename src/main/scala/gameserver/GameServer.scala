package gameserver

import akka.actor._
import scala.collection.mutable.HashMap
import tournament._
import tournament.plan._
import gameserver.ports.PortService
import messages._

class GameServer extends Actor{
	var portService: ActorRef = null
	var tournaments: HashMap[String, ActorRef] = new HashMap
	var remoteNode: akka.remoteinterface.RemoteServerModule = null
	val remoteNodePort: Int = 9999

	override def preStart() {
		portService = Actor.actorOf(new PortService(10000))
		portService.start
		log.info("GameServer: portService started")
		remoteNode = Actor.remote.start("localhost", remoteNodePort)
		
	}


	def receive = {
		case ServerStart() =>
//			startFakeTournament(remoteNodePort, remoteNode, "fakeTournament1")
//			log.info("gameServer: fakeTournament2 started for testing purposes.")
//			startFakeTournament(remoteNodePort, remoteNode, "fakeTournament2")
//			log.info("gameServer: fakeTournament2 started for testing purposes.")

			startTestTournament(remoteNode)
			log.info("GameServer: testTournament1 started")
			startTestTournament(remoteNode)
			log.info("GameServer: testTournament2 started")



		case RequestPorts(amount: Int) =>
			log.info("got port request. forwarding...")
			portService.forward(RequestPorts(amount: Int))

		case ReleasePorts(portList: List[Int]) =>
			log.info("got ports to release. forwarding...")
			portService.forward(ReleasePorts(portList: List[Int]))

		case RequestTag() =>
			log.info("got a tag request. forwarding...")
			portService.forward(RequestTag())
	
		case WebTest() =>
			self.reply("Webtest seems to work")

		case _ => 
			println("unknown message received")
			val reply: String = "<a href='http://akka.io'>Akka Actors rock!</a>"
			self.reply(reply)
			
	}

	def startTestTournament(remoteNode: akka.remoteinterface.RemoteServerModule): Unit = {
  		val plan = new DummyPlan
    		val tournament = Actor.actorOf(new Tournament(plan, self, remoteNode))
		val tournamentName = (portService !! _root_.messages.RequestTournamentName()).getOrElse(throw new RuntimeException("TIMEOUT")).asInstanceOf[String]
		log.info("GameServer: created a new tournament with the name " + tournamentName)
		tournaments += tournamentName.asInstanceOf[String] -> tournament
		tournament.start
		tournament ! _root_.messages.Start()

	}
	
	def startFakeTournament(port: Int, testServer: akka.remoteinterface.RemoteServerModule, name: String): Unit = {
		println("startFaketournament entered")
		val fakeTournament = Actor.actorOf(new testStuff.TestFakeTournament(port, testServer, name))
		fakeTournament.start
		fakeTournament ! _root_.testStuff.TestStart()
	}


////////////////////// REST Connection Stuff //////////////////

	def getTournaments: Unit = {

	}

	def getTournament(tournamentIdentifier: String): Unit = {

	}

	def getGames(tournamentIdentifier: String): Unit = {
		
	}

	def getGame(tournamentIdentifier: String, gameIdentifier: String): Unit = {

	}


////////////////////////////////////////////////////////////////


}


object RunGameServer {
	def main(args: Array[String]) {

		val gameServer = Actor.actorOf(new GameServer)
		gameServer.start

		val gamePort = (gameServer !! _root_.messages.RequestPorts(1)).getOrElse(throw new RuntimeException("RunGameServer: TIMEOUT"))
		println("to test the portservice: the port I requested is: " + (gamePort match {case l: List[Int] => l(0) }).toString  )
		
//		var webServer = new _root_.ch.ethz.inf.vs.projectname.JerseyMain()
//		webServer.myServerStarter(gameServer)

		gameServer ! _root_.messages.ServerStart()
	}
}




 
