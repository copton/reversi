package mechanics

import reversi.{Position,Occupation,Color}

trait Board {
  import Board._

  def getOccupation(position: Position): Occupation
  protected def setOccupation(position: Position, occupation: Occupation): Unit

  def copyFrom(source: Board): Unit = {
    for (pos <- Positions) {
      setOccupation(pos, source.getOccupation(pos))
    }
  }

  override def toString(): String = {
    val s: StringBuffer = new StringBuffer
    s.append("\n")
    for (pos <- Positions) {
      s.append(getOccupation(pos))
      if (pos.y == size-1) {
        s.append("\n")
      }
    }
    s.toString()
  }
}

object Board {
  val size = 8
  val Positions = (for (x <- 0 to size-1; y <- 0 to size-1) yield new Position(x,y)).toList
}

trait ArrayBoard extends Board {
  val fields: Array[Array[Occupation]] = Array.fill(Board.size, Board.size)(Occupation.FREE)
  def getOccupation(pos: Position): Occupation = fields(pos.x)(pos.y)
  def setOccupation(pos: Position, occupation: Occupation): Unit = fields(pos.x).update(pos.y, occupation)
}

trait DefaultBoard {this: Board =>
  setOccupation(new Position(3,3), Color.GREEN)
  setOccupation(new Position(3,4), Color.RED)
  setOccupation(new Position(4,3), Color.RED)
  setOccupation(new Position(4,4), Color.GREEN) 
}
