package mechanics
package test

import org.specs._

import _root_.reversi.{Color, Occupation, Position}
import _root_.mechanics.{Board, ArrayBoard, DefaultBoard}

class DefaultReversiGameBoardSpec extends Specification {

  val centerA = 3
  val centerB = 4
  val board = new ArrayBoard with DefaultBoard {}
  
  "default reversi board has red and green in starting positions" in {
    board.getOccupation(new Position(centerA, centerA)) must be equalTo(Color.GREEN)
    board.getOccupation(new Position(centerA, centerB)) must be equalTo(Color.RED)
    board.getOccupation(new Position(centerB, centerA)) must be equalTo(Color.RED)
    board.getOccupation(new Position(centerB, centerB)) must be equalTo(Color.GREEN)
  }

  "default reversi board only has 4 stone in the center" in {
    var centerPositions = List(new Position(centerA, centerA),
                               new Position(centerA, centerB),
                               new Position(centerB, centerA),
                               new Position(centerB, centerB))

    for (position <- centerPositions) {
      board.getOccupation(position) must not be equalTo(Occupation.FREE) 
    }
    for (position <- Board.Positions filterNot (centerPositions contains)) {
      board.getOccupation(position) must be equalTo(Occupation.FREE) 
    }
  }
}

// vim: set ts=2 sw=2 et:
