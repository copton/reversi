package game

import akka.actor.{Actor, ActorRef}
//import akka.remote
import akka.util.Logging
import scala.collection.mutable.HashMap
import scala.collection.immutable.List
import reversi.{Color, Position}
import tournament.misc.DummyGameDetails
import tournament.misc.GameDetails
import tournament.misc.GameResult
import tournament.misc.DummyGameResult


class Player(val name: String, val port: Int, val color: Color, val namingNumber: Int) {
	var actor: Option[ActorRef] = None
	var proc: Option[PlayerProc] = None
  var ready = false
}

object GameFactory {
	private def createPlayer(className: String, playerPort: Int, playerColor: Color, namingNumber: Int): Player = {
	
		return new Player(className, playerPort, playerColor, namingNumber);
	}

	def createGame(gamePort: Int, playerPorts: List[Int], details: GameDetails, tournament: ActorRef, namingNumber: Int): ActorRef = {
		val player1 = createPlayer(details.players(0), playerPorts(0), details  match {case d: DummyGameDetails => d.additionalInformation(0) match {case c: Color => c} }, namingNumber) //TODO add exception case
		val player2 = createPlayer(details.players(1), playerPorts(1), details  match {case d: DummyGameDetails => d.additionalInformation(1) match {case c: Color => c} }, namingNumber)
		Actor.actorOf(new Game(gamePort, Array(player1, player2), tournament))
	}
}

class Game(val gamePort: Int, val players: Array[Player], tournament: ActorRef) extends Actor with Logging {
  
  assert ((players map (_.port)).distinct.size == players.size, "each player needs a different port!")

  var board = new player.GameBoard()
  var nextPlayer = 0
  var lastMove: Option[Position] = None
  var possibleMoves: List[Position] = Nil
  var nextMovePending = false

  override def preStart() {
    for (player <- players) {
      player.proc = Some(new PlayerProc(player, self, gamePort))
      log.info("PreStart-Message from the Game: " + "Player " + player.name + " started! The port is " + player.port)
    }
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
	val gameResult = new DummyGameResult
      	if (redCount == greenCount) {log.info("draw game"); gameResult.winner = "draw game"}
      	else if (redCount > greenCount) {log.info("RED player wins with " + redCount + " to " + greenCount + "."); gameResult.winner = "red"}
      	else {log.info("GREEN player wins with " + greenCount + " to " + redCount + "."); gameResult.winner = "green"}
	var portsToReturn: List[Int] = players(0).port::players(1).port::Nil
	tournament ! GameFinished(gameResult, self, portsToReturn)
    } else {
      	player.actor.get ! _root_.player.RequestNextMove(board, lastMove)
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
      			log.info("Player " + player.name + " started! The port is " + player.port)
    		}

  	case Started(port) =>
        	val player = getPlayer(port)
      		log.info("Player " + player.name + " (" + port + ") started")
      		player.actor = Some(self.sender.get)
      		player.actor.get ! _root_.player.LoadPlayer(player.name, player.color)
    

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
