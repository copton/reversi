package player

import java.lang.Thread

class Proxy(val player: reversi.Player) extends reversi.GameControler {
  private var decision: Option[reversi.Position] = None
  
  override def update(position: reversi.Position) = decision = Some(position)

  def nextMove(): Option[reversi.Position] = {
    None
  }
}

class PlayerThread(val player: reversi.Player) extends Thread {
  override def run() {
    
  }
}

class TimeoutThread(val thread: Thread) extends Thread {
  override def run() {
    Thread.sleep(5000)
     
  }
}
