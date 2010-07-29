import se.scalablesolutions.akka.actor.Actor
import se.scalablesolutions.akka.actor.ActorRef
import se.scalablesolutions.akka.actor.Actor._
import se.scalablesolutions.akka.remote.{RemoteClient, RemoteNode}
import se.scalablesolutions.akka.util.Logging

class Player(val game: ActorRef) extends Actor {
  def receive = {
	case "Run" => game ! "Started"
    case "Hello" =>
      log.info("Received 'Hello'")
      self.reply("World")
  }
}


object Server extends Logging {
	def main(args: Array[String]) {
		log.info("MARK 1")
		val game = RemoteClient.actorFor("game", "localhost", args(0).toInt)
		log.info("MARK 2")
		val player = actorOf(new Player(game))
		player.start
		player ! "Run"
		log.info("MARK 3")
	}
}
