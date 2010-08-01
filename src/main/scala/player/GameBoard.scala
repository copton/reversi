package player

import reversi.{GameBoard => _, _}
import mechanics._

class GameBoard extends reversi.GameBoard with DefaultReversiGameBoard with ReversiGameMechanics {
    override def getOccupation(pos: Position): Occupation = Occupation.FREE
   
    override def checkMove(pos: Position, color: Color): Boolean = true
   
    override def makeMove(pos: Position, color: Color): GameBoard = this
}
