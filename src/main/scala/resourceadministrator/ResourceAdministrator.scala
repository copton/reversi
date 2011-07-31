package resourceadministrator

import akka.actor._
import java.util.Stack
import java.util.LinkedList
import scala.collection.immutable.List
import gameserver._
import messages._

class ResourceAdministrator(var basePort: Int, var maxGames: Int) extends Actor{

	val freePorts: Stack[Int] = new Stack()
	var tournamentNameCounter: Int = 1
	var currentGames: Int = 0
	var waitingActors: LinkedList[Option[ActorRef]] = new LinkedList()		


	def receive = {

		case RequestPermission() =>
			val requester = Some(self.sender.get)
			if(currentGames < maxGames) {
				requester.get ! _root_.messages.PermissionGranted()
				currentGames = currentGames + 1
			} else {
				waitingActors.offer(requester)
			}

		case ReleasePermission() =>
			if(waitingActors.isEmpty) {			
				currentGames = currentGames - 1
			} else {
				var next = waitingActors.poll()
				next.get ! _root_.messages.PermissionGranted()
			}
			

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


		case _ => println("PortService: unknown message received")
	}


}
