package player
import reversi.Position

class GameControler extends reversi.GameControler {
	private var position: Option[Position] = None

	def pass(): Unit = {
		done()		
	}

	def update(pos: Position): Unit = {
		position = Some(pos)
	}

	def done(): Unit = {
			
	}
}
