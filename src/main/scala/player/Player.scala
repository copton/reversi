package player

import se.scalablesolutions.akka.actor.Actor
import se.scalablesolutions.akka.actor.ActorRef
import se.scalablesolutions.akka.actor.Actor._
import se.scalablesolutions.akka.remote.{RemoteClient, RemoteNode}
import se.scalablesolutions.akka.util.Logging

case object Run

class Player(val name: String, val game: ActorRef) extends Actor {
  def receive = {
	case Run => game ! _root_.game.Started(name)
    case "Hello" =>
      log.info("Received 'Hello'")
      self.reply("World")
  }
}

object RunPlayer extends Logging {
	def main(args: Array[String]) {
		val name = args(0)
		val playerPort = args(1).toInt
		val gamePort = args(2).toInt
		
		RemoteNode.start("localhost", playerPort)
		val game = RemoteClient.actorFor("game", "localhost", gamePort)
		val player = actorOf(new Player(name, game))

		player.start
		player ! Run
	}
}
