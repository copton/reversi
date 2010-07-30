package player
import reversi.{GameBoard => _, _}
import annotation._

class GameBoard extends reversi.GameBoard {
	val board : Array[Array[Occupation]] = Array.fill(reversi.GameBoard.size, reversi.GameBoard.size)(Occupation.FREE)

	val dirs = for (x <- -1 to 1; y <- -1 to 1; if (! (x==0 && y==0))) yield new Position(x, y)

    def getOccupation(pos: Position): Occupation = board(pos.x)(pos.y)

	@tailrec
	private def followDirection(pos: Position, dir: Position, col: Color): Boolean = {
		val newPos = pos.add(dir)
		val otherColor = Color.other(col)
		if (! newPos.isValid) false
		else 
			getOccupation(newPos) match {
				case col => true
				case Occupation.FREE => false
				case otherColor => followDirection(newPos, dir, col)
			}
	}

	private def checkDirection(pos: Position, dir: Position, col: Color): Boolean = {
		val newPos = pos.add(dir)
		val otherColor = Color.other(col)
		newPos.isValid && getOccupation(newPos) == otherColor && followDirection(newPos, dir, col)
	}
   
    def checkMove(pos: Position, color: Color): Boolean =
		if (getOccupation(pos) != Occupation.FREE) {
			false
		} else {
			dirs.foldLeft(false)((ac, dir) => ac || checkDirection(pos, dir, color))
		}
   
    def makeMove(pos: Position, color: Color): GameBoard = {
		this
	}
   
    def countStones(color: Color): Int = (for {
		column <- board
		occupation <- column
		if occupation == color } yield occupation).length
}
