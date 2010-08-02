package player

import java.lang.Thread

class Proxy(val player: reversi.Player) extends reversi.GameControler {
  private var decision: Option[reversi.Position] = None

  def update(position: reversi.Position) = decision = Some(position)
  
  def nextMove(board: reversi.GameBoard, lastMove: reversi.Position): Option[reversi.Position] = {
    val thread = new PlayerThread(player, board, lastMove, this)
    thread.start()
    thread.join()
    return decision
  }
}

class PlayerThread(val player: reversi.Player, val board: reversi.GameBoard, val lastMove: reversi.Position, val controler: reversi.GameControler) extends Thread {
  override def run() {
    player.nextMove(board, lastMove, controler)
  }
}
