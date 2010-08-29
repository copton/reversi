package mechanics

import annotation._
import _root_.reversi.{GameBoard => _, _}

trait Rules { this: Board =>
  import Rules._ 

  @tailrec
  private def followDirection(pos: Position, dir: Direction, color: Color): Boolean = {
    val newPos = pos add dir
    if (! newPos.isValid)
      false
    else 
      getOccupation(newPos) match {
        case Occupation.FREE => false
        case c: Occupation if c == color => true
        case c: Occupation if c == color.other => followDirection(newPos, dir, color)
      }
  }

  private def checkDirection(pos: Position, dir: Direction, color: Color): Boolean = {
    val newPos = pos add dir
    val otherColor = color.other
    newPos.isValid && getOccupation(newPos) == otherColor && followDirection(newPos, dir, color)
  }

  private def getMoves(pos: Position, color: Color): List[Direction] =
    if (getOccupation(pos) != Occupation.FREE) {
      Nil
    } else {
      Directions.filter(checkDirection(pos, _, color))
    }

  @tailrec
  private def flipStones(pos: Position, dir: Position, color: Color): Unit = {
    getOccupation(pos) match {
      case o: Occupation if o != color => 
        setOccupation(pos, color)
        flipStones(pos add dir, dir, color)
      case _ => 
    }
  }

  def performMakeMove(pos: Position, color: Color): Unit = {
    val moves = getMoves(pos, color)
    if(moves.isEmpty) {
      throw new reversi.GameBoard.InvalidMoveException
    } else {
      for(dir <- moves) flipStones(pos, dir, color)
    }
  }

  def checkMove(pos: Position, col: Color): Boolean = ! getMoves(pos, col).isEmpty

  def getPossibleMoves(color: Color): List[Position] = Board.Positions.filter(getMoves(_, color) != Nil)
}

object Rules {
  val Directions = (for (x <- -1 to 1; y <- -1 to 1; if (! (x==0 && y==0))) yield new Position(x, y)).toList
  type Direction = Position
}

// vim: set ts=2 sw=2 et:
