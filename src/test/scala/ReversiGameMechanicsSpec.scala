package test

import org.specs._

import _root_.reversi.{Color, Occupation, GameBoard, Position}
import _root_.mechanics.DefaultReversiGameBoard
import _root_.mechanics.ReversiGameMechanics._

class ReversiGameMechanicsSpec extends Specification {

  val centerA = 3
  val centerB = 4

  "countStones(GREEN) on default board returns 2" in {
    val board = new DefaultReversiGameBoard {}
    countStones(board.board, Color.GREEN) must be equalTo(2)
  }

  "countStones(RED) on default board returns 2" in {
    val board = new DefaultReversiGameBoard {}
    countStones(board.board, Color.RED) must be equalTo(2)
  }

  "simple move on default board results in one direction" in {
    val board = new DefaultReversiGameBoard {}
    getMoves(board.board, new Position(centerB, centerB + 1), Color.RED) must
      haveTheSameElementsAs(List(new Direction(0, -1)))
    getMoves(board.board, new Position(centerA, centerB + 1), Color.GREEN) must
      haveTheSameElementsAs(List(new Direction(0, -1)))
    getMoves(board.board, new Position(centerB + 1, centerA), Color.GREEN) must
      haveTheSameElementsAs(List(new Direction(-1, 0)))
    getMoves(board.board, new Position(centerA - 1, centerB), Color.GREEN) must
      haveTheSameElementsAs(List(new Direction(1, 0)))
  }

  "simple faulty move on default board results in empty direction list" in {
    val board = new DefaultReversiGameBoard {}
    getMoves(board.board, new Position(centerB + 1, centerA), Color.RED) must
      haveTheSameElementsAs(Nil)
    getMoves(board.board, new Position(centerA, centerA - 1), Color.GREEN) must
      haveTheSameElementsAs(Nil)
  }

  "simple move flips one stone" in {
    val board = new DefaultReversiGameBoard {}
    val validation = new DefaultReversiGameBoard {}
    validation.board(centerB).update(centerB, Color.RED)
    validation.board(centerB).update(centerB + 1, Color.RED)
    val newBoard = makeMove(board.board, new Position(centerB, centerB + 1), Color.RED)
    for(x <- List.range(0, 8); y <- List.range(0, 8)) {
      newBoard(x)(y) must be equalTo(validation.board(x)(y))
    }
  }
}

// vim: set ts=2 sw=2 et:
