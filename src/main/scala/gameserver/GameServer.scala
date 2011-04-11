package gameserver

import akka.actor._
import tournament._
import gameserver.ports.PortService

class GameServer extends Actor{
	
	val portService = Actor.actorOf(new PortService)
	portService.start

	def receive = {
		case RequestPorts(amount: Int) =>
			portService.forward(RequestPorts(amount: Int))
	
		case _ => println("unknown message received")
			
	}

}
