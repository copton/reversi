package player

import se.scalablesolutions.akka.actor.Actor
import se.scalablesolutions.akka.actor.ActorRef
import se.scalablesolutions.akka.actor.Actor._
import se.scalablesolutions.akka.remote.{RemoteClient, RemoteNode, RemoteServer}
import se.scalablesolutions.akka.util.Logging
import java.net.InetSocketAddress

class Player(val port: Int, val game: ActorRef) extends Actor {
  var proxy: Option[Proxy] = None

  override def init() {
	  game ! _root_.game.Started(port)
  }

  def receive = {
    case LoadPlayer(name, color) => 
      val player = util.PlayerLoader.load(name)
      player.initialize(color)
      proxy = Some(new Proxy(player))
      game ! _root_.game.PlayerReady(port)

    case RequestNextMove(board, lastMove) =>
      val position = proxy.get.nextMove(board, lastMove)
      game ! _root_.game.ReportNextMove(position)
  }
}

object RunPlayer extends Logging {
	def main(args: Array[String]) {
		val playerPort = args(0).toInt
		val gamePort = args(1).toInt
		val game = RemoteClient.actorFor("game", "localhost", gamePort)

    System.out.println("MARK 1")

    val playerServer = new RemoteServer
    playerServer.start("localhost", playerPort)
    val player = Actor.actorOf(new Player(playerPort, game))
    playerServer.register("player" + playerPort, player)
    player.start
  }
}
