package test

import org.specs._

import _root_.reversi.{Occupation}
import _root_.mechanics.DefaultReversiGameBoard
import _root_.mechanics.ReversiGameMechanics._

class ReversiGameMechanicsSpec extends Specification {

  "countStones(GREEN) on default board returns 2" in {
    val board = new DefaultReversiGameBoard {}
    countStones(board.board, Occupation.GREEN) must be equalTo(2)
  }

  "countStones(RED) on default board returns 2" in {
    val board = new DefaultReversiGameBoard {}
    countStones(board.board, Occupation.RED) must be equalTo(2)
  }

  "countStones(FREE) on default board returns 60" in {
    val board = new DefaultReversiGameBoard {}
    countStones(board.board, Occupation.FREE) must be equalTo(60)
  }


}

// vim: set ts=2 sw=2 et:
