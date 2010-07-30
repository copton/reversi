package player


import reversi.{GameBoard => _, _}
import mechanics._

class GameBoard extends reversi.GameBoard with DefaultReversiGameBoard with ReversiGameMechanics {

  def checkMove(pos: Position, color: Occupation): Boolean = getMoves(board, pos, color).isEmpty
 
  def makeMove(pos: Position, color: Occupation): GameBoard = this

  def getOccupation(pos: Position): Occupation = getOccupation(board, pos)

  def countStones(color: Occupation): Int = countStones(board, color)

  def dump(): String = dump(board)
}
