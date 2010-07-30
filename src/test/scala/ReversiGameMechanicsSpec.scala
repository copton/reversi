package test

import org.specs._

import _root_.reversi.{Occupation, GameBoard, Position}
import _root_.mechanics.DefaultReversiGameBoard
import _root_.mechanics.ReversiGameMechanics._

class ReversiGameMechanicsSpec extends Specification {

  val centerA = (GameBoard.size / 2) - 1
  val centerB = (GameBoard.size / 2)

  "countStones(GREEN) on default board returns 2" in {
    val board = new DefaultReversiGameBoard {}
    countStones(board.board, Occupation.GREEN) must be equalTo(2)
  }

  "countStones(RED) on default board returns 2" in {
    val board = new DefaultReversiGameBoard {}
    countStones(board.board, Occupation.RED) must be equalTo(2)
  }

  "countStones(FREE) on default board returns " in {
    val board = new DefaultReversiGameBoard {}
    val size = (GameBoard.size * GameBoard.size) - 4
    countStones(board.board, Occupation.FREE) must be equalTo(size)
  }

  "simple move on default board results in one direction" in {
    val board = new DefaultReversiGameBoard {}
    getMoves(board.board, new Position(centerB, centerB + 1), Occupation.RED) must
      haveTheSameElementsAs(List(new Direction(0, -1)))
    getMoves(board.board, new Position(centerA, centerB + 1), Occupation.GREEN) must
      haveTheSameElementsAs(List(new Direction(0, -1)))
    getMoves(board.board, new Position(centerB + 1, centerA), Occupation.GREEN) must
      haveTheSameElementsAs(List(new Direction(-1, 0)))
    getMoves(board.board, new Position(centerA - 1, centerB), Occupation.GREEN) must
      haveTheSameElementsAs(List(new Direction(1, 0)))
  }

  "simple faulty move on default board results in empty direction list" in {
    val board = new DefaultReversiGameBoard {}
    getMoves(board.board, new Position(centerB + 1, centerA), Occupation.RED) must
      haveTheSameElementsAs(Nil)
    getMoves(board.board, new Position(centerA, centerA - 1), Occupation.GREEN) must
      haveTheSameElementsAs(Nil)
  }

  "foop" in {
    val board = new DefaultReversiGameBoard {}
    val validation = new DefaultReversiGameBoard {}
    validation.board(centerB).update(centerB, Occupation.RED)
    validation.board(centerB).update(centerB + 1, Occupation.RED)
    println(dump(makeMove(board.board, new Position(centerB, centerB + 1), Occupation.RED)))
    makeMove(board.board, new Position(centerB, centerB + 1), Occupation.RED) must be equalTo(validation.board)
  }
}

// vim: set ts=2 sw=2 et:
