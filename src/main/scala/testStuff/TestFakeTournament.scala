package testStuff

import akka.actor._

class TestFakeTournament(val port: Int, var testServer: akka.remoteinterface.RemoteServerModule,val name: String) extends Actor {

//	var testServer: akka.remoteinterface.RemoteServerModule = null

	override def preStart() = {
		
//		testServer = Actor.remote.start("localhost", port)
//		println("testServer started")
		
	}


	def receive = {
		case TestStart() =>
			var fakeGame = Actor.actorOf(new TestFakeGame(port, name))
			testServer.register(name, fakeGame)
			println("fakeGame registered on testServer")




	}


}

class TestFakeGame(val port: Int, val name: String) extends Actor {

	override def preStart() = {
		var fakePlayer = Actor.actorOf(new TestFakePlayer(port, name))
		fakePlayer.start
		println("fakePlayer started")
	}

	def receive = {	
		case Ping() =>
			println("Ping "+name+" "+port)
			self.reply(Pong())

	}

}


class TestFakePlayer(val port: Int, val name: String) extends Actor {
	
	override def preStart() {
		println("fakePlayer preStart")
		val fakeGame = Actor.remote.actorFor(name, "localhost", port)		
		fakeGame ! Ping()

		
	}

	def receive = {
		case Pong() => 
			println("Pong "+name+" "+port)
			Thread.sleep(1000)
			self.reply(Ping())
		case _ => println("message received")
	}
}

sealed trait Message

case class TestStart() extends Message
case class Ping() extends Message
case class Pong() extends Message
