package player

import reversi.{Occupation, Position, Color}
import mechanics._

class GameBoard extends reversi.GameBoard with Rules with ArrayBoard with DefaultBoard with java.io.Serializable with java.lang.Cloneable {
  override def makeMove(pos: Position, color: Color): GameBoard = {
    val newBoard = new GameBoard
    newBoard.copyFrom(this)
    newBoard.performMakeMove(pos, color)
    newBoard
  }

  override def clone(): player.GameBoard = {
//	super.clone().asInstanceOf[player.GameBoard]
	val newBoard = new GameBoard
	newBoard.copyFrom(this)
	newBoard
  }
}
