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

	var testServer, testServer2: akka.remoteinterface.RemoteServerModule = null

	override def preStart() {
		portService = Actor.actorOf(new PortService(10000))
		portService.start
		log.info("GameServer: portService started")
//		testServer = Actor.remote.start("localhost", 9999)
//		testServer2 = Actor.remote.start("localhost", 8888)
		
	}


	def receive = {
		case ServerStart() =>
//			startFakeTournament(8888)
//			log.info("gameServer: fakeTournament started for testing purposes.")
//			startFakeTournament(9999, testServer, "fakeTournament1")
//			log.info("gameServer: fakeTournament2 started for testing purposes.")
//			startFakeTournament(9999, testServer, "fakeTournament2")
//			log.info("gameServer: fakeTournament2 started for testing purposes.")
//			startFakeTournament(5555)
//			log.info("gameServer: fakeTournament2 started for testing purposes.")
			startTestTournament
			log.info("GameServer: testTournament1 started")



		case RequestPorts(amount: Int) =>
			log.info("got port request. forwarding...")
			portService.forward(RequestPorts(amount: Int))

		case ReleasePorts(portList: List[Int]) =>
			log.info("got ports to release. forwarding...")
			portService.forward(ReleasePorts(portList: List[Int]))
	
		case _ => println("unknown message received") 
			
	}

	def startTestTournament: Unit = {
  		val plan = new DummyPlan
    		val tournament = Actor.actorOf(new Tournament(plan, self))
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


////////////////////////////////////////////////////////////////


}


object RunGameServer {
	def main(args: Array[String]) {

		val gameServer = Actor.actorOf(new GameServer)
		gameServer.start
		gameServer ! _root_.messages.ServerStart()

		val gamePort = (gameServer !! _root_.messages.RequestPorts(1)).getOrElse(throw new RuntimeException("RunGameServer: TIMEOUT"))
		println("to test the portservice: the port I requested is: " + (gamePort match {case l: List[Int] => l(0) }).toString  )

	}


} 
