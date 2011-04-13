package gameserver.ports

import akka.actor._
import java.util.Stack
import tournament._
import scala.collection.immutable.List

class PortService(var basePort: Int) extends Actor{

	val freePorts: Stack[Int] = new Stack()


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
			self.reply(portList)

		case ReleasePorts(portList: List[Int]) =>
			for(port <- portList) {
				freePorts.push(port)
			}

		case _ => println("PortService: unknown message received")
	}


}
