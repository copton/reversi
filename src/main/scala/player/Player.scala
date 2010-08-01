package player

import se.scalablesolutions.akka.actor.Actor
import se.scalablesolutions.akka.actor.ActorRef
import se.scalablesolutions.akka.actor.Actor._
import se.scalablesolutions.akka.remote.{RemoteClient, RemoteNode}
import se.scalablesolutions.akka.util.Logging
import player.FromGameToPlayer._
import player.FromPlayerToGame._

class Player(val port: Int, val game: ActorRef) extends Actor {
  var proxy: Option[Proxy] = None

  override def init() {
	  game ! FromPlayerToGame.Started(port)
  }

  def receive = {
    case LoadPlayer(name, color) => 
      val player = util.PlayerLoader.load(name)
      player.initialize(color)
      proxy = Some(new Proxy(player))
      game ! PlayerReady()

    case RequestNextMove(lastMove) =>
      game ! ReportNextMove(proxy.get.nextMove())
  }
}

object RunPlayer extends Logging {
	def main(args: Array[String]) {
		val playerPort = args(0).toInt
		val gamePort = args(1).toInt
		
		RemoteNode.start("localhost", playerPort)
		val game = RemoteClient.actorFor("game", "localhost", gamePort)
		val player = actorOf(new Player(playerPort, game))

		player.start
	}
}
