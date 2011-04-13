package gameserver

import akka.actor._
import tournament._
import tournament.plan._
import gameserver.ports.PortService

class GameServer extends Actor{
	var portService: ActorRef = null
	

	override def preStart() {
		portService = Actor.actorOf(new PortService(10000))
		portService.start
		log.info("GameServer: portService started")
	}


	def receive = {
		case Start() =>
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
		tournament.start
		tournament ! Start()

	}

}


object RunGameServer {
	def main(args: Array[String]) {

		val gameServer = Actor.actorOf(new GameServer)
		gameServer.start
		gameServer ! Start()

		val gamePort = (gameServer !! RequestPorts(1)).getOrElse(throw new RuntimeException("RunGameServer: TIMEOUT"))
		println("to test the portservice: the port I requested is: " + (gamePort match {case l: List[Int] => l(0) }).toString  )

	}


} 
