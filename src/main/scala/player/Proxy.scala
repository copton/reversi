package player

import java.lang.Thread
import se.scalablesolutions.akka.util.Logging

class Proxy(val player: reversi.Player) extends reversi.GameControler with Logging {
  private var decision: Option[reversi.Position] = None

  def logger(text: String) {
    log.info("from player: " + text)
  }

  def update(position: reversi.Position) {
      decision = Some(position)
  }
  
  def nextMove(board: reversi.GameBoard, lastMove: Option[reversi.Position]): Option[reversi.Position] = {
    val thread = new PlayerThread(player, board, lastMove, this)
    thread.start()
    thread.join()
    return decision
  }
}

class PlayerThread(val player: reversi.Player, val board: reversi.GameBoard, val lastMove: Option[reversi.Position], val controler: reversi.GameControler) extends Thread {
  override def run() {
    if (lastMove.isDefined) {
      player.nextMove(board, lastMove.get, controler)
    } else {
      player.nextMove(board, null, controler)
    }
  }
}
