package game

import akka.actor.{Actor, ActorRef}
//import akka.remote
import akka.util.Logging
import scala.collection.immutable.TreeMap
import scala.collection.immutable.List
import scala.math.Ordering
import reversi.{Color, Position}
import tournament._
import tournament.misc.GameDetails
import tournament.misc.DummyGameOption
import player._
import java.net.InetSocketAddress
import messages._


class Player(val name: String, val port: Int, val color: Color, val uniqueTag: String) {
	var actor: Option[ActorRef] = None
	var proc: Option[PlayerProc] = None
  	var ready = false
}

trait gameCreation {
	def createGame(playerPorts: List[Int], details: GameDetails, tournament: ActorRef, uniqueTag: String): ActorRef
}

object GameFactory extends gameCreation {
	private def createPlayer(className: String, playerPort: Int, playerColor: Color, uniqueTag: String): Player = {
	
		return new Player(className, playerPort, playerColor, uniqueTag);
	}

	def createGame(playerPorts: List[Int], details: GameDetails, tournament: ActorRef, uniqueTag: String): ActorRef = {
		val player1 = createPlayer(details.players(0), playerPorts(0), details.options(0).asInstanceOf[DummyGameOption].colors(0), uniqueTag) 
		val player2 = createPlayer(details.players(1), playerPorts(1), details.options(0).asInstanceOf[DummyGameOption].colors(1), uniqueTag)
		Actor.actorOf(new Game(9999, Array(player1, player2), tournament, uniqueTag))
	}
}

class Game(val gamePort: Int, val players: Array[Player], tournament: ActorRef, name: String) extends Actor with Logging {
  
	assert ((players map (_.port)).distinct.size == players.size, "each player needs a different port!")

	var board = new player.GameBoard()
	var nextPlayer = 0
	var lastMove: Option[Position] = None	
	var possibleMoves: List[Position] = Nil
  	var nextMovePending = false
	var winner: String = "not yet known"
	
	var turnNumber: Int = 1
	var turns: TreeMap[String, String] = new TreeMap()(new Ordering[String]{
		override def compare(s1: String, s2: String): Int = {
			if( s1.substring(4, s1.length).toInt >= s2.substring(4, s2.length).toInt ) {
				return 1
			} else {
				return -1
			}
		}
	})


  	private def nextMove() {
    		assert (nextMovePending == false , "game is in wrong state! nextMove is already pending")
    		nextMovePending = true
 
    		val player = players(nextPlayer)
    		possibleMoves = board.getPossibleMoves(player.color)

		turns += "turn" + turnNumber.toString() -> board.toString()
		

  
    		if (possibleMoves == Nil && board.getPossibleMoves(Color.other(player.color)) == Nil) {
    			log.info("game finished:" + board)
    			val redCount = board.countStones(Color.RED)
		     	val greenCount = board.countStones(Color.GREEN)
		      	if (redCount == greenCount) {log.info("draw game"); winner = "draw game"}
		      	else if (redCount > greenCount) {log.info("RED player wins with " + redCount + " to " + greenCount + "."); winner = "RED"}
		      	else {log.info("GREEN player wins with " + greenCount + " to " + redCount + "."); winner = "Green"}			

			//cleanup, destroying connections, ...
			var portsToReturn: List[Int] = players(0).port::players(1).port::Nil
	 		for (player <- players) {
				player.actor.get ! _root_.messages.KillPlayer()
	      			log.info("Game: I sended a KillPlayer() Message to " + "Player " + player.name + " The port is " + player.port)
				Actor.remote.shutdownClientConnection(new InetSocketAddress("localhost", player.port))	
	    		}

			tournament ! _root_.messages.GameFinished(self, portsToReturn, players(0).uniqueTag)
		} else {
		      	player.actor.get ! _root_.messages.RequestNextMove(board, lastMove)
		      	nextPlayer = (nextPlayer + 1) % players.size
			turnNumber = turnNumber + 1
		}
	}

  	private def getPlayer(port: Int): Player = {
      		val playerOpt = players.find(_.port == port) 
      		assert (playerOpt.isDefined, "Received message from unknown player " + port)
      		playerOpt.get
	}


  	def receive = {
			

  		case StartGame() =>
			for (player <- players) {
				log.info("Game: StartGame() received")
      				player.proc = Some(new PlayerProc(player, self, gamePort))
      				log.info("Player " + player.name + "-proc started! The port is " + player.port)
    			}

  		case Started(port) =>
        		val player = getPlayer(port)
      			log.info("Player " + player.name + " (" + port + ") started")
      			player.actor = Some(self.sender.get)
      			player.actor.get ! _root_.messages.LoadPlayer(player.name, player.color)
    

  		case PlayerReady(port) =>
      			val player = getPlayer(port)
      			assert (player.ready == false, "Player " + port + " is already ready")
      			player.ready = true
      			log.info("Player " + player.name + " (" + port + ") is ready")
      			if (players.forall(_.ready == true)) {
        			log.info("All players are ready. Start game!")
        			nextMove()
      			}

    		case ReportNextMove(port, position) =>
      			assert (nextMovePending, "Unexpected ReportNextMove received")
      			nextMovePending = false
      			val player = getPlayer(port) 
      			if (possibleMoves == Nil) {
        			position match {
          				case None => 
            					log.info("player " + port + " passes")
            					nextMove()
          				case Some(pos) =>
            					log.info("Illegal move by player " + port + ": player's move is " + pos + ", but no move is possible on board " + board)
        			}
      			} else {
        			position match {
          				case None =>
            					log.info("Illegal move by player " + port + ": player passed, but moves are possible on board " + board)
          				case Some(pos) =>
            				log.info("player " + port + " makes move at " + pos)
            				board.performMakeMove(pos, player.color)
            				nextMove()
        			}
      			}

  		case PlayerExit(player, exitCode) =>
      			player.proc.get.join()
      			log.info("Player " + player.name + " exited with exit code " + exitCode.toString + ".")
			
		case WebLoadTurnCollection() =>
			println("loading turncollection...")
			var result: String = ""
			turns foreach ( (t1) => result = result + t1._1 + "\n")
			self.reply(result)

		case WebGetGame() =>
			self.reply("the winner is: " + winner)
		
		case WebGetTurn(turn: String) =>
			var result: String = turns(turn)
			self.reply(result)

		case WebGetCurrentTurn() =>
			var result: CurrentTurnReply = new CurrentTurnReply(board.clone, null)
			self.reply(result)
			

    		case msg => log.info("received message: " + msg)
  	}
}

//object RunGame {
//	def main(args: Array[String]) {
//	  val gamePort = 10000	
//    val gameServer = Actor.remote.start("localhost", gamePort)
//
//    val playerRed = new Player("player.RandomPlayer", gamePort + 1, Color.RED)
//    val playerGreen = new Player("player.RandomPlayer", gamePort + 2, Color.GREEN)
//    val game = Actor.actorOf(new Game(gamePort, Array(playerRed, playerGreen)))
//    gameServer.register("game"+gamePort, game)
//	}
//}
