package game

import tournament.misc._

sealed trait Message

// from Tournement

case class StartGame() extends Message
	//Startmessage

case class GetTurnInformation(turnNumber: Int) extends Message
	//requests information about turn number 'turnNumber'

// from Player
case class Started(port: Int) extends Message
case class PlayerReady(port: Int) extends Message
case class ReportNextMove(port: Int, position: Option[reversi.Position]) extends Message

// from PlayerProc
case class PlayerExit(player: Player, exitCode: Int)
