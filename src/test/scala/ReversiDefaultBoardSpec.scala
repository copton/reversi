package test

import org.specs._

import _root_.reversi.{Occupation, GameBoard}
import _root_.mechanics.DefaultReversiGameBoard

class DefaultReversiGameBoardSpec extends Specification {

  val centerA = (GameBoard.size / 2) - 1
  val centerB = (GameBoard.size / 2)
  val board = new DefaultReversiGameBoard {}
  
  "default reversi board has red and green in starting positions" in {
    board.board(centerA)(centerA) must be equalTo(Occupation.GREEN)
    board.board(centerA)(centerB) must be equalTo(Occupation.RED)
    board.board(centerB)(centerA) must be equalTo(Occupation.RED)
    board.board(centerB)(centerB) must be equalTo(Occupation.GREEN)
  }

  "default reversi board only has 4 stone in the center" in {
    board.board(centerA)(centerA) must not be equalTo(Occupation.FREE)
    board.board(centerA)(centerB) must not be equalTo(Occupation.FREE)
    board.board(centerB)(centerA) must not be equalTo(Occupation.FREE)
    board.board(centerB)(centerB) must not be equalTo(Occupation.FREE)
    for {
      x <- 0 to GameBoard.size - 1
      y <- 0 to GameBoard.size - 1
      if(!((x >= 3 && x <= 4) &&
           (y >= 3 && y <= 4)))
    } { board.board(x)(y) must be equalTo(Occupation.FREE) }

  }
}

// vim: set ts=2 sw=2 et:
