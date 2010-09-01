package mechanics
package test

import org.specs._

import _root_.reversi.{Color, Occupation, Position}
import _root_.player.GameBoard
import _root_.mechanics.Board

class ReversiGameMechanicsSpec extends Specification {

  val centerA = 3
  val centerB = 4
  val board = new GameBoard()

  "countStones(GREEN) on default board returns 2" in {
    board.countStones(Color.GREEN) must be equalTo(2)
  }

  "countStones(RED) on default board returns 2" in {
    board.countStones(Color.RED) must be equalTo(2)
  }

  "on a default board there are 4 possible moves" in {
    Board.Positions.filter(board.checkMove(_, Color.RED)) must
      haveTheSameElementsAs(List(
            new Position(2, 3),
            new Position(3, 2),
            new Position(5, 4),
            new Position(4, 5)
     ))
  }

  "simple move on default board results in one direction" in {
    board.getMoves(new Position(centerB, centerB + 1), Color.RED) must
      haveTheSameElementsAs(List(new Rules.Direction(0, -1)))
    board.getMoves(new Position(centerA, centerB + 1), Color.GREEN) must
      haveTheSameElementsAs(List(new Rules.Direction(0, -1)))
    board.getMoves(new Position(centerB + 1, centerA), Color.GREEN) must
      haveTheSameElementsAs(List(new Rules.Direction(-1, 0)))
    board.getMoves(new Position(centerA - 1, centerB), Color.GREEN) must
      haveTheSameElementsAs(List(new Rules.Direction(1, 0)))
  }

  "simple faulty move on default board results in empty direction list" in {
    board.getMoves(new Position(centerB + 1, centerA), Color.RED) must
      haveTheSameElementsAs(Nil)
    board.getMoves(new Position(centerA, centerA - 1), Color.GREEN) must
      haveTheSameElementsAs(Nil)
  }

  "simple move flips one stone" in {
    val validation = new GameBoard()
    validation.setOccupation(new Position(centerB, centerB), Color.RED)
    validation.setOccupation(new Position(centerB, centerB + 1), Color.RED)
    val newBoard = board.makeMove(new Position(centerB, centerB + 1), Color.RED)
    for (pos <- Board.Positions) {
      newBoard.getOccupation(pos) must be equalTo(validation.getOccupation(pos))
    }
  }
}

// vim: set ts=2 sw=2 et:
