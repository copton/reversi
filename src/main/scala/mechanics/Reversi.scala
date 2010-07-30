package mechanics

import annotation._
import _root_.reversi.{GameBoard => _, _}

trait ReversiGameMechanics {

  type Board = Array[Array[Occupation]]

  type Direction = Position
  val dirs = for (x <- -1 to 1; y <- -1 to 1; if (! (x==0 && y==0))) yield new Direction(x, y)

  @tailrec
  private def followDirection(board: Board, pos: Position, dir: Position, col: Color): Boolean = {
    val newPos = pos.add(dir)
      val otherColor = Color.other(col)
      if (! newPos.isValid) false
    else 
      getOccupation(board, newPos) match {
      case c: Color if c == col => true
      case Occupation.FREE => false
      case otherColor => followDirection(board, newPos, dir, col)
    }
  }

  private def checkDirection(board: Board, pos: Position, dir: Position, col: Color): Boolean = {
    val newPos = pos.add(dir)
    val otherColor = Color.other(col)
    newPos.isValid && getOccupation(board, newPos) == otherColor && followDirection(board, newPos, dir, col)
  }

  def getMoves(board: Board, pos: Position, color: Color): List[Direction] =
    if (getOccupation(board, pos) != Occupation.FREE) {
      Nil
    } else {
      dirs.foldLeft(Nil:List[Direction])((ac, dir) => if(checkDirection(board, pos, dir, color)) dir::ac else ac)
    }

  def countStones(board: Board, color: Color): Int =
    (for {
      column <- board
      occupation <- column
      if occupation == color
    } yield occupation).length

  def getOccupation(board: Board, pos: Position): Occupation = board(pos.x)(pos.y)
}

object ReversiGameMechanics extends ReversiGameMechanics

trait DefaultReversiGameBoard {
  val board : ReversiGameMechanics.Board = Array.fill(reversi.GameBoard.size, reversi.GameBoard.size)(Occupation.FREE)

  board(3).update(3, Color.GREEN)
  board(3).update(4, Color.RED)
  board(4).update(3, Color.RED)
  board(4).update(4, Color.GREEN)
}

// vim: set ts=2 sw=2 et:
