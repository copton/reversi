package messages

import akka.actor._
import scala.collection.immutable.List
import tournament.misc._
import tournament.plan.TurnInfo
import game.Player

sealed trait Message

//tournament
	//from server
	case class Start() extends Message
	case class GetGames()
	case class GetGame(gameIdentifier: String)

	//from game
	case class GameFinished(result: GameResult, game: ActorRef, portsToRelease: List[Int], namingNumber: Int) extends Message

	// Information about a requested turn
	case class ReturnTurnInformation(info: TurnInfo) extends Message


//player
	// from Game
	case class LoadPlayer(name: String, color: reversi.Color) extends Message
	case class RequestNextMove(board: reversi.GameBoard, lastMove: Option[reversi.Position]) extends Message
	case class KillPlayer() extends Message

	//from RunPlayer
	case class TestStart() extends Message


//gameserver
	//from Tournament
	case class RequestPorts(amount: Int) extends Message
	case class ReleasePorts(portList: List[Int]) extends Message

	//from starting mechanism
	case class ServerStart() extends Message

	//from webserver
	case class WebTest() extends Message
	case class WebGetRoot() extends Message
	case class WebGetTournaments() extends Message
	case class WebGetTournament(tournamentIdentifier: String) extends Message
	case class WebGetGame(tournamentIdentifier: String, gameIdentifier: String) extends Message
	case class WebGetTurn(tournamentIdentifier: String, gameIdentifier: String, turnNumber: Int) extends Message



//portservice
	//from requester
	case class RequestTournamentName() extends Message
	case class RequestTag() extends Message


//game
	// from Tournement
	case class StartGame() extends Message	//Startmessage
	case class GetTurnInformation(turnNumber: Int) extends Message	//requests information about turn number 'turnNumber'
	
	// from Player
	case class Started(port: Int) extends Message
	case class PlayerReady(port: Int) extends Message
	case class ReportNextMove(port: Int, position: Option[reversi.Position]) extends Message

	// from PlayerProc
	case class PlayerExit(player: Player, exitCode: Int)
