package game

import akka.actor._
import tournament.misc._

sealed trait Message

// from Tournement (to Game) ----> Propably in the constructor

//case class StartGame(players: List[String], gameDetails: GameDetails) extends Message
  case class StartGame() extends Message

// from Game (to Tournement)

case class GameFinished(result: GameResult, game: ActorRef, portsToRelease: List[Int]) extends Message
case class SomethingWentWrong(error: GameError) extends Message

// from Player
case class Started(port: Int) extends Message
case class PlayerReady(port: Int) extends Message
case class ReportNextMove(port: Int, position: Option[reversi.Position]) extends Message

// from PlayerProc
case class PlayerExit(player: Player, exitCode: Int)
