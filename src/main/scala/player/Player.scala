package player

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Actor._
//import akka.remote.{RemoteClient, RemoteNode, RemoteServer}
import akka.util.Logging
import java.net.InetSocketAddress

class Player(val port: Int, val game: ActorRef) extends Actor {
  var proxy: Option[Proxy] = None

  self.homeAddress = ("localhost", port)

  override def init() {
	  game ! _root_.game.Started(port)
  }

  def receive = {
    case LoadPlayer(name, color) => 
      val player = util.PlayerLoader.load(name)
      val logPrefix = "from player " + port + "(" + color + "): "
      player.initialize(color)
      proxy = Some(new Proxy(player, logPrefix))
      game ! _root_.game.PlayerReady(port)

    case RequestNextMove(board, lastMove) =>
      val position = proxy.get.nextMove(board, lastMove)
      game ! _root_.game.ReportNextMove(port, position)
  }
}

object RunPlayer extends Logging {
	def main(args: Array[String]) {
		val playerPort = args(0).toInt
		val gamePort = args(1).toInt
    val game = Actor.remote.actorFor("game", "localhost", gamePort)

    val player = Actor.actorOf(new Player(playerPort, game))
    player.start
  }
}
