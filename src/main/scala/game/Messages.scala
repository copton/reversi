package game

sealed trait Message

// from Player
case class Started(port: Int) extends Message
case class PlayerReady(port: Int) extends Message
case class ReportNextMove(position: Option[reversi.Position]) extends Message

// from PlayerProc
case class PlayerExit(player: Player, exitCode: Int)

// temporary
case class RunPlayer(player: Player)

