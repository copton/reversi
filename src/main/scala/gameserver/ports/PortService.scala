package gameserver.ports

import akka.actor._
import java.util.Stack
import scala.collection.immutable.List
import gameserver._
import messages._

class PortService(var basePort: Int) extends Actor{

	val freePorts: Stack[Int] = new Stack()

	var tournamentNameCounter: Int = 1
	var gameNameCounter: Int = 1		


	def receive = {
		case RequestPorts(amount: Int) =>
			var portList: List[Int] = Nil
			for (i <- 1 to amount){
				if(freePorts.empty()) {
					portList = basePort::portList
					basePort = basePort + 1
				} else {
					portList = freePorts.pop()::portList
				}
				
			}
			println("PortService: giving out following ports: " + portList)		
			self.reply(portList)

		case ReleasePorts(portList: List[Int]) =>
			for(port <- portList) {
				freePorts.push(port)
			}

		case RequestTournamentName() =>
			self.reply("tournament"+tournamentNameCounter)
			tournamentNameCounter = tournamentNameCounter + 1

		case RequestGameName() =>
			self.reply("game"+gameNameCounter)
			gameNameCounter = gameNameCounter + 1


		case _ => println("PortService: unknown message received")
	}


}
