package game

import se.scalablesolutions.akka.actor.{Actor, ActorRef}
import se.scalablesolutions.akka.remote.RemoteServer
import se.scalablesolutions.akka.util.Logging
import scala.collection.mutable.HashMap


class Player(val name: String, val port: Int, val color: reversi.Color) {
	var actor: Option[ActorRef] = None
	var proc: Option[PlayerProc] = None
}

class Game(val gamePort: Int) extends Actor with Logging {
	val players: HashMap[Int, Player] = new HashMap()

	def receive = {
		case RunPlayer(player) => 
      players.get(player.port) match {
        case Some(_) =>
          log.error("Player " + player.port + " already exists!")
        case None =>
          players += player.port -> player
          player.proc = Some(new PlayerProc(player, self, gamePort))
          log.info("Player " + player.name + " started!")
      }

		case Started(port) =>
      players.get(port) match {
        case None => 
          log.error("Player " + port + " is not known")
        case Some(player) =>
          log.info("Player " + player.name + " started")
          player.actor = Some(self.sender.get)
          player.actor.get ! _root_.player.LoadPlayer(player.name, player.color)
			}

    case PlayerReady(port) =>
      log.info("Player " + port + " is ready")

    case PlayerExit(player, exitCode) =>
      player.proc.get.join()
      log.info("Player " + player.name + " exited with exit code " + exitCode.toString + ".")
	
  }
}

object RunGame {
	def main(args: Array[String]) {
	  val gamePort = 10000	
    val gameServer = new RemoteServer
    gameServer.start("localhost", gamePort)
    val game = Actor.actorOf(new Game(gamePort))
    gameServer.register("game", game)
    
		game.start
		game ! RunPlayer(new Player("player.RandomPlayer", gamePort + 1, reversi.Color.RED))
	}
}