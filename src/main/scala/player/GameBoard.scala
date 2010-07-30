package player
import reversi.{GameBoard => _, _}
import annotation._

class GameBoard extends reversi.GameBoard {
	val board : Array[Array[Occupation]] = Array.fill(reversi.GameBoard.size, reversi.GameBoard.size)(Occupation.FREE)
    board(3).update(3, Color.GREEN)
    board(3).update(4, Color.RED)
    board(4).update(3, Color.RED)
    board(4).update(4, Color.GREEN)

    type Direction = Position
	val dirs = for (x <- -1 to 1; y <- -1 to 1; if (! (x==0 && y==0))) yield new Direction(x, y)

    def getOccupation(pos: Position): Occupation = board(pos.x)(pos.y)

	@tailrec
	private def followDirection(pos: Position, dir: Position, col: Color): Boolean = {
		val newPos = pos.add(dir)
		val otherColor = Color.other(col)
		if (! newPos.isValid) false
		else 
			getOccupation(newPos) match {
				case c: Color if c == col => true
				case Occupation.FREE => false
				case otherColor => followDirection(newPos, dir, col)
			}
	}

	private def checkDirection(pos: Position, dir: Position, col: Color): Boolean = {
		val newPos = pos.add(dir)
		val otherColor = Color.other(col)
		newPos.isValid && getOccupation(newPos) == otherColor && followDirection(newPos, dir, col)
	}
   
    private def getMoves(pos: Position, color: Color): List[Direction] =
		if (getOccupation(pos) != Occupation.FREE) {
			Nil
		} else {
			dirs.foldLeft(Nil:List[Direction])((ac, dir) => if(checkDirection(pos, dir, color)) dir::ac else ac)
		}

    def checkMove(pos: Position, color: Color): Boolean = getMoves(pos, color).isEmpty
   
    def makeMove(pos: Position, color: Color): GameBoard = {
		this
	}
   
    def countStones(color: Color): Int = (for {
		column <- board
		occupation <- column
		if occupation == color } yield occupation).length
}
