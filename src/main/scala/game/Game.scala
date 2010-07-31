package game

import se.scalablesolutions.akka.actor.Actor
import se.scalablesolutions.akka.actor.ActorRef
import se.scalablesolutions.akka.actor.Actor._
import se.scalablesolutions.akka.remote.{RemoteClient, RemoteNode}
import se.scalablesolutions.akka.util.Logging
import java.lang.Thread
import scala.collection.mutable.HashMap


class Player(val name: String, val port: Int) {
	var actor: Option[ActorRef] = None
	var proc: Option[PlayerProc] = None
}

case class RunPlayer(player: Player)
case class Started(name: String)

object Game {
	val port: Int = 10000
}

class Game extends Actor with Logging {
	val players: HashMap[String, Player] = new HashMap()

	def receive = {
		case RunPlayer(player) => 
			if (players.contains(player.name)) {
				log.error("Player " + player.name + " already exists!")
			} else {
				players += player.name -> player
				player.proc = Some(new PlayerProc(player, Game.port))
				log.error("Player " + player.name + " started!")
			}

		case Started(name) =>
			if (players.contains(name)) {
				log.info("Player " + name + " started")
				val sender = self.sender.get
				players(name).actor = Some(sender)
				println(sender !! "Hello")
			} else {
				log.error("Player " + name + " is not known")
			}
	}
}

object RunGame {
	def main(args: Array[String]) {
		val game = actorOf[Game]
		
		RemoteNode.start("localhost", Game.port)
		RemoteNode.register("game", game)

		game.start
		game ! RunPlayer(new Player("TestPlayer", Game.port + 1))
	}
}
