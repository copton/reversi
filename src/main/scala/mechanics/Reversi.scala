package mechanics

import annotation._
import _root_.reversi.{GameBoard => _, _}

trait ReversiGameMechanics {

  type Board = Array[Array[Occupation]]
  type Direction = Position

  val Directions = for (x <- -1 to 1; y <- -1 to 1; if (! (x==0 && y==0))) yield new Direction(x, y)

  @tailrec
  private def followDirection(board: Board, pos: Position, dir: Position, col: Occupation): Boolean = {
    val newPos = pos add dir
    if (! newPos.isValid)
      false
    else 
      getOccupation(board, newPos) match {
        case Occupation.FREE => false
        case c: Occupation if c == col => true
        case c: Occupation if c == col.other => followDirection(board, newPos, dir, col)
      }
  }

  private def checkDirection(board: Board, pos: Position, dir: Position, col: Occupation): Boolean = {
    val newPos = pos add dir
    val otherColor = col.other
    newPos.isValid && getOccupation(board, newPos) == otherColor && followDirection(board, newPos, dir, col)
  }

  def getMoves(board: Board, pos: Position, color: Occupation): List[Direction] =
    if (getOccupation(board, pos) != Occupation.FREE) {
      Nil
    } else {
      Directions.foldLeft(Nil:List[Direction])((ac, dir) => if(checkDirection(board, pos, dir, color)) dir::ac else ac)
    }

  def countStones(board: Board, color: Occupation): Int =
    (for {
      column <- board
      occupation <- column
      if occupation == color
    } yield occupation).length

  def getOccupation(board: Board, pos: Position): Occupation = board(pos.x)(pos.y)

  @tailrec
  private def setColor(board: Board, pos: Position, dir: Position, color: Occupation): Unit = {
    getOccupation(board, pos) match {
      case o: Occupation if o != color => 
        board(pos.x).update(pos.y, color)
        setColor(board, pos add dir, dir, color)
      case _ => 
    }
  }

  def makeMove(board: Board, pos: Position, color: Occupation): Board = {
    val moves = getMoves(board, pos, color)
    if(!moves.isEmpty) {
      val newBoard = board.clone
      for(dir <- moves) setColor(newBoard, pos, dir, color)
      newBoard
    } else board
  }

  def dump(board: Board): String = {
    val s: StringBuffer = new StringBuffer
    for (x <- 0 to reversi.GameBoard.size - 1) {
      for(y <- 0 to reversi.GameBoard.size - 1) {
        s.append(
          getOccupation(board, new Position(x, y)) match {
            case Occupation.FREE  => ". "
            case Occupation.GREEN => "O "
            case Occupation.RED   => "X "
        })
      }
      s.append("\n")
    }
    s.toString()
  }
}

object ReversiGameMechanics extends ReversiGameMechanics

trait DefaultReversiGameBoard {
  val board : ReversiGameMechanics.Board = Array.fill(reversi.GameBoard.size, reversi.GameBoard.size)(Occupation.FREE)

  {
    val centerB = (reversi.GameBoard.size / 2)
    val centerA = centerB - 1
    board(centerA).update(centerA, Occupation.GREEN)
    board(centerA).update(centerB, Occupation.RED)
    board(centerB).update(centerA, Occupation.RED)
    board(centerB).update(centerB, Occupation.GREEN)
  }
}

// vim: set ts=2 sw=2 et:
