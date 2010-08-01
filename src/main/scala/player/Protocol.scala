package player

sealed trait Message;

object FromGameToPlayer {
  case class LoadPlayer(name: String, color: reversi.Color) extends Message
	case class RequestNextMove(lastMove: reversi.Position) extends Message
}

object FromPlayerToGame {
	case class Started(port: Int) extends Message
	case class PlayerReady() extends Message	
	case class ReportNextMove(position: Option[reversi.Position]) extends Message
}
