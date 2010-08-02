package player

sealed trait Message

// from Game
case class LoadPlayer(name: String, color: reversi.Color) extends Message
case class RequestNextMove(board: reversi.GameBoard, lastMove: reversi.Position) extends Message
