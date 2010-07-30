package player


import reversi.{GameBoard => _, _}
import mechanics._

class GameBoard extends reversi.GameBoard with DefaultReversiGameBoard with ReversiGameMechanics {

  def checkMove(pos: Position, color: Color): Boolean = getMoves(board, pos, color).isEmpty
 
  def makeMove(pos: Position, color: Color): GameBoard = this

  def getOccupation(pos: Position): Occupation = getOccupation(board, pos)

  def countStones(color: Color): Int = countStones(board, color)

  def dump(): String = dump(board)
}
