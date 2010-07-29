import se.scalablesolutions.akka.actor.Actor
import se.scalablesolutions.akka.actor.ActorRef
import se.scalablesolutions.akka.actor.Actor._
import se.scalablesolutions.akka.remote.{RemoteClient, RemoteNode}
import se.scalablesolutions.akka.util.Logging

class Player(val game: ActorRef) extends Actor {
  game ! "Started" 
  def receive = {
    case "Hello" =>
      log.info("Received 'Hello'")
      self.reply("World")
  }
}


object Server extends Logging {
	def main(args: Array[String]) {
		val game = RemoteClient.actorFor("game", "localhost", args(0).toInt)
		Actor.actorOf(new Player(game)).start
	}
}
