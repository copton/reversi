package testStuff

import akka.actor._
import akka.util.Logging
import scala.util.Random

class ActorTest extends Actor {
	val random = new Random()
	println("ActorTest Started!");

	
	def receive = {
		case  TestMessage(i: Int) => println("ActorTest: received message number " + i)
		case _ => println("huh?")
	}

}

case class TestMessage(i: Int)

object RunActorTest {
	def main(args: Array[String]) {
	  val gamePort = 10000	
	  val gameServer = Actor.remote.start("localhost", gamePort)
	  gameServer.start("localhost", gamePort)

    
	  val actorTest = Actor.actorOf(new ActorTest)
	  gameServer.register("actorTest", actorTest)
	  Thread.sleep(2000)
	  var i = 0
	  val random = new Random()
	  while (true) {
		  actorTest ! TestMessage(i)
		  i = i+1
		  println("RunActorTest: TestMessage sent");
		  Thread.sleep(500 + Random.nextInt(2000))
	  }
	}
}
