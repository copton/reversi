package game

import akka.actor.{Actor, ActorRef}
//import akka.remote
import akka.util.Logging
import java.util.ArrayList
import scala.collection.immutable.TreeMap
import scala.collection.immutable.List
import scala.math.Ordering
import reversi.{Color, Position, Occupation}
import tournament._
import tournament.misc.GameDetails
import tournament.misc.DummyGameOption
import tournament.misc.DummyGameResult
import player._
import java.net.InetSocketAddress
import messages._


class Player(val name: String, val port: Int, val color: Color, val uniqueTag: String) {
	var actor: Option[ActorRef] = None
	var proc: Option[PlayerProc] = None
  	var ready = false
	var log: ArrayList[String] = new ArrayList()
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

	var finished: Boolean = false

	var board = new player.GameBoard()
	var nextPlayer = 0
	var lastMove: Option[Position] = None	
	var possibleMoves: List[Position] = board.getPossibleMoves(Color.RED)
  	var nextMovePending = false
	var winner: String = "not yet known"
	var ssPlayer: Player = null
	
	var turnNumber: Int = 1
	var turns: TreeMap[String, GameboardSnapshot] = new TreeMap()(new Ordering[String]{
		override def compare(s1: String, s2: String): Int = {
			if( s1.substring(4, s1.length).toInt >= s2.substring(4, s2.length).toInt ) {
				return 1
			} else {
				return -1
			}
		}
	})
	
	turns += "turn0" -> createBaseSnapshot

	def createBaseSnapshot: GameboardSnapshot = {
		var snapshot = new GameboardSnapshot
		snapshot.insertBoardInformations(this.board)
		snapshot.insertPossibleMoves(this.possibleMoves)
		return snapshot
	}

	def createSnapshot: Unit = {
	
		var snapshot = new GameboardSnapshot
		ssPlayer.color match {
			case Color.RED => {snapshot.playercolor = Color.RED; println("setting playercolor to red")}
			case Color.GREEN =>{ snapshot.playercolor = Color.GREEN; println("setting playercolor to green")}
			case _ => println("the turd option")
		}
		snapshot.turn = "turn" + turnNumber.toString
		snapshot.insertBoardInformations(this.board)
		snapshot.insertPossibleMoves(this.possibleMoves)
		snapshot.redStones = this.board.countStones(Color.RED)
		snapshot.greenStones = this.board.countStones(Color.GREEN)	
		snapshot.insertPlayerMove(lastMove)
		turns += "turn" + turnNumber.toString -> snapshot

		turnNumber = turnNumber + 1
	}


  	private def nextMove() {
    		assert (nextMovePending == false , "game is in wrong state! nextMove is already pending")
    		nextMovePending = true
 

    		val player = players(nextPlayer)
    		possibleMoves = board.getPossibleMoves(player.color)		

		
  
    		if (possibleMoves == Nil && board.getPossibleMoves(Color.other(player.color)) == Nil) {
    			log.info("game finished:" + board)
    			val redCount = board.countStones(Color.RED)
		     	val greenCount = board.countStones(Color.GREEN)
		      	if (redCount == greenCount) {log.info("draw game"); winner = "draw game"}
		      	else if (redCount > greenCount) {log.info("RED player wins with " + redCount + " to " + greenCount + "."); winner = "RED"}
		      	else {log.info("GREEN player wins with " + greenCount + " to " + redCount + "."); winner = "Green"}			

			finished = true

			//cleanup, destroying connections, ...
			var portsToReturn: List[Int] = players(0).port::players(1).port::Nil
	 		for (player <- players) {
				player.actor.get ! _root_.messages.KillPlayer()
	      			log.info("Game: I sended a KillPlayer() Message to " + "Player " + player.name + " The port is " + player.port)
				Actor.remote.shutdownClientConnection(new InetSocketAddress("localhost", player.port))	
	    		}
			//Delivering the Result of the Game
			val result = new DummyGameResult
			result.winner = winner
			result.board = board.clone
			tournament ! _root_.messages.GameFinished(result, portsToReturn, players(0).uniqueTag)
		} else {
		      	player.actor.get ! _root_.messages.RequestNextMove(board, lastMove)
		      	nextPlayer = (nextPlayer + 1) % players.size
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

    		case ReportNextMove(port, position, playerLog) =>
      			assert (nextMovePending, "Unexpected ReportNextMove received")
			
      			nextMovePending = false
			lastMove = position
      			val player = getPlayer(port)
			player.log.addAll(playerLog)
			ssPlayer = player
			println(ssPlayer.color.toString)
	
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
			createSnapshot
      			}

  		case PlayerExit(player, exitCode) =>
      			player.proc.get.join()
      			log.info("Player " + player.name + " exited with exit code " + exitCode.toString + ".")
			
		case WebLoadTurnCollection() =>
			println("loading turncollection...")
			var result = new ArrayList[String]
			turns foreach ( (t1) => result.add(t1._1) )
			self.reply(result)

		case WebGetGame() =>
			self.reply("the winner is: " + winner)
		
		case WebGetTurn(turn: String) =>
			val xturnNumber: Int = turn.substring(4, turn.length).toInt
			val nextTurn: String = "turn" + (xturnNumber+1).toString
			val previousTurn: String = "turn" + (xturnNumber-1).toString
			var nextExists: Boolean = turns.contains(nextTurn)
			var previousExists: Boolean = turns.contains(previousTurn)
			var result: TurnReply= new TurnReply(xturnNumber, nextExists, previousExists, name, turns(turn) )
			self.reply(result)

		case WebGetCurrentTurn(lastTurn: String) =>
			println("game: lastturn is " + lastTurn)
			val xturnNumber: Int = lastTurn.substring(4, lastTurn.length).toInt
			val nextTurn: String = "turn" + (xturnNumber + 1).toString
			if(turns.contains(nextTurn)) {
				self.reply(new CurrentTurnReply(turns(nextTurn)))
			} else {
				self.reply(new CurrentTurnReply(turns(lastTurn)))
			}

		case WebLoadPlayerCollection() =>
			var result = new ArrayList[String]
			players(0).color match {
				case Color.RED => result.add("Red")
				case Color.GREEN => result.add("Green")
				case _ => result.add("No color attached to this player!")
			}
			players(1).color match {
				case Color.RED => result.add("Red")
				case Color.GREEN => result.add("Green")
				case _ => result.add("No color attached to this player!")
			}
			self.reply(result)

		case WebGetPlayer(player: String) =>
			var playerOpt: Option[game.Player] = null
			player match {
				case "Red" => playerOpt = players.find(_.color == Color.RED)
				case "Green" => playerOpt = players.find(_.color == Color.GREEN)
				case _ =>

			}
			var result = new PlayerReply(playerOpt.get.log)
			self.reply(result)
			
			
    		case msg => log.info("received message: " + msg)
  	}
}

class GameboardSnapshot {

	var turn: String = "turn0"

	var playercolor: Color = null //player who did THIS Move
	var redStones: Int = 2
	var greenStones: Int = 2	

	var playField = Array.fill[String] (8,8) ("nothing")

	def insertBoardInformations (board: _root_.player.GameBoard) {
		for (i <- 0 to 7) {
			for (j <- 0 to 7) {
				val pos: Position = new Position(i,j)
				val occ: Occupation = board.getOccupation(pos)
				occ match {
					case Color.RED => playField(i)(j) = "red"
					case Color.GREEN => playField(i)(j) = "green"
					case _ => playField(i)(j) = "nothing"
				}
			}
		}
		redStones = board.countStones(Color.RED)
		greenStones = board.countStones(Color.GREEN)		
	}


	def insertPossibleMoves(possibleMoves: List[Position]): Unit =  {
		for(move <- possibleMoves){
			val x = move.x
			val y = move.y
			playField(x)(y) = "possible"
		}
	}

	def insertPlayerMove(playerMove: Option[Position]): Unit = {
		playerMove match {
			case Some(move) => {
				val lastX = move.x
				val lastY = move.y

				playercolor match {
					case Color.RED => playField(lastX)(lastY) = "redmark"
					case Color.GREEN => playField(lastX)(lastY) = "greenmark"
					case _ => //nothing
				}

				
			}
			case None => //Do Nothing
		}
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
