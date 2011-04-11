package gameserver.ports

import akka.actor._
import tournament._
import scala.collection.immutable.List

class PortService extends Actor{

	var  basePort: Int = 1000
	


	def receive = {
		case RequestPorts(amount: Int) =>
			var portList: List[Int] = Nil
			for (i <- 1 to amount){
				basePort::portList
				basePort = basePort + 1
			}		
			self.reply(portList)

		case _ => println("unknown message received")
	}


}
