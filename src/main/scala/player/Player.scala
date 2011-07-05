package player

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Actor._
//import akka.remote.{RemoteClient, RemoteNode, RemoteServer}
import akka.util.Logging
import java.util.ArrayList
import java.net.InetSocketAddress
import game._
import messages._


class Player(val port: Int, val game: ActorRef, val gamePort: Int) extends Actor {
  var proxy: Option[Proxy] = None

  var playerLog: ArrayList[String] = new ArrayList()

  //self.homeAddress = ("localhost", port)

  override def preStart() {
//	  game ! "Player can send messages to Game"
//	  game ! _root_.messages.Started(port)
  }

  override def postStop() {
	

  }

  def receive = {
    case PlayerStart() =>
	game ! _root_.messages.Started(port)
    case LoadPlayer(name, color) => 
      val player = util.PlayerLoader.load(name)
      val logPrefix = "from player " + port + "(" + color + "): "
      player.initialize(color)
      proxy = Some(new Proxy(player, logPrefix))
      game ! _root_.messages.PlayerReady(port)

    case RequestNextMove(board, lastMove) =>
      val position = proxy.get.nextMove(board, lastMove)
      playerLog.add("player moved to " + position.toString)
      game ! _root_.messages.ReportNextMove(port, position, playerLog)
      playerLog.clear
      

    case KillPlayer() =>
	log.info("player: KillPlayer() received. calling exit")
	exit

  }
}

object RunPlayer extends Logging {
	def main(args: Array[String]) {
		val playerPort = args(0).toInt
		val gamePort = args(1).toInt
		val uniqueTag = args(2)
    		val game = Actor.remote.actorFor(uniqueTag, "localhost", gamePort)
    		val player = Actor.actorOf(new Player(playerPort, game, gamePort))

    		val playerServer = Actor.remote.start("localhost", playerPort)
    		playerServer.register(uniqueTag+"/player", player)
		player ! _root_.messages.PlayerStart()
  	}
}
