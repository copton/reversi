package mechanics

import annotation._
import _root_.reversi.{GameBoard => _, _}

trait ReversiGameMechanics {
  import ReversiGameMechanics._

  @tailrec
  private def followDirection(board: Board, pos: Position, dir: Direction, color: Color): Boolean = {
    val newPos = pos add dir
    if (! newPos.isValid)
      false
    else 
      getOccupation(board, newPos) match {
        case Occupation.FREE => false
        case c: Occupation if c == color => true
        case c: Occupation if c == color.other => followDirection(board, newPos, dir, color)
      }
  }

  private def checkDirection(board: Board, pos: Position, dir: Direction, color: Color): Boolean = {
    val newPos = pos add dir
    val otherColor = color.other
    newPos.isValid && getOccupation(board, newPos) == otherColor && followDirection(board, newPos, dir, color)
  }

  def getMoves(board: Board, pos: Position, color: Color): List[Direction] =
    if (getOccupation(board, pos) != Occupation.FREE) {
      Nil
    } else {
      Directions.filter(checkDirection(board, pos, _, color))
    }

  def countStones(board: Board, color: Color): Int =
    (for {
      column <- board
      occupation <- column
      if occupation == color
    } yield occupation).length

  def getOccupation(board: Board, pos: Position): Occupation = board(pos.x)(pos.y)

  @tailrec
  private def flipStones(board: Board, pos: Position, dir: Position, color: Color): Unit = {
    getOccupation(board, pos) match {
      case o: Occupation if o != color => 
        board(pos.x).update(pos.y, color)
        flipStones(board, pos add dir, dir, color)
      case _ => 
    }
  }

  def makeMove(board: Board, pos: Position, color: Color): Board = {
    val moves = getMoves(board, pos, color)
    if(!moves.isEmpty) {
      val newBoard = board.clone
      for(dir <- moves) flipStones(newBoard, pos, dir, color)
      newBoard
    } else board
  }

  def dump(board: Board): String = {
    val s: StringBuffer = new StringBuffer
    for (x <- 0 to 7) {
      for(y <- 0 to 7) {
        s.append(
          getOccupation(board, new Position(x, y)) match {
            case Occupation.FREE  => ". "
            case Color.GREEN => "O "
            case Color.RED=> "X "
        })
      }
      s.append("\n")
    }
    s.toString()
  }
}

object ReversiGameMechanics extends ReversiGameMechanics {
  val Directions = (for (x <- -1 to 1; y <- -1 to 1; if (! (x==0 && y==0))) yield new Position(x, y)).toList

  type Board = Array[Array[Occupation]]
  type Direction = Position

}

trait DefaultReversiGameBoard {
  val board : ReversiGameMechanics.Board = Array.fill(8, 8)(Occupation.FREE)
  board(3).update(3, Color.GREEN)
  board(3).update(4, Color.RED)
  board(4).update(3, Color.RED)
  board(4).update(4, Color.GREEN)
}

// vim: set ts=2 sw=2 et:
