package game

import tournement.misc._

sealed trait Message

// from Tournement (to Game)

case class StartGame(players: List[String]) extends Message

// from Game (to Tournement)

case class GameFinished(result: GameResult) extends Message
case class SomethingWentWrong(error: GameError) extends Message

// from Player
case class Started(port: Int) extends Message
case class PlayerReady(port: Int) extends Message
case class ReportNextMove(port: Int, position: Option[reversi.Position]) extends Message

// from PlayerProc
case class PlayerExit(player: Player, exitCode: Int)
