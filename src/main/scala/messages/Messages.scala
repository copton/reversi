package messages

import akka.actor._
import java.util.ArrayList
import scala.collection.immutable.List
import tournament.misc._
import game.Player

sealed trait Message

//tournament
	//from server
	case class Start() extends Message
	case class Stop(plan: _root_.tournament.plan.Plan) extends Message
	
	//from game
	case class GameFinished(game: GameResult, portsToRelease: List[Int], namingNumber: String) extends Message
	case class EmergencyFinished(portsToRelease: List[Int]) extends Message

	//from ressourceAdministrator
	case class PermissionGranted() extends Message

	//from webserver
	case class WebLoadGameCollection() extends Message
	case class WebGetTournament() extends Message


//player
	// from Game
	case class LoadPlayer(name: String, color: reversi.Color) extends Message
	case class RequestNextMove(board: reversi.GameBoard, lastMove: Option[reversi.Position]) extends Message
	case class KillPlayer() extends Message

	//from RunPlayer
	case class PlayerStart() extends Message // Sets player in Motion


//gameserver

	//from starting mechanism
	case class ServerStart() extends Message

	//from webserver
	case class WebTest() extends Message
	case class WebGetRoot() extends Message
	case class WebLoadTournamentCollection() extends Message
	case class WebGetTournaments() extends Message
	case class WebPostForTournament(tournamentName: String, postArgument: String) extends Message
	case class WebRequestActor(actorName: String) extends Message




//resourceAdministrator

	//from requester
	case class RequestTournamentName() extends Message
	case class RequestPorts(amount: Int) extends Message
	case class ReleasePorts(portList: List[Int]) extends Message
	case class RequestPermission() extends Message
	case class ReleasePermission() extends Message

//game
	// from Tournament
	case class StartGame() extends Message
	case class EmergencyExit() extends Message
	
	// from Player
	case class Started(port: Int) extends Message
	case class PlayerReady(port: Int) extends Message
	case class ReportNextMove(port: Int, position: Option[reversi.Position], playerLog: ArrayList[String]) extends Message

	// from PlayerProc
	case class PlayerExit(player: Player, exitCode: Int)

	//from web
	case class WebLoadTurnCollection() extends Message
	case class WebGetGame() extends Message
	case class WebGetTurn(turn: String) extends Message
	case class WebGetCurrentTurn(lastTurn: String) extends Message
	case class WebLoadPlayerCollection() extends Message
	case class WebGetPlayer(player: String) extends Message
