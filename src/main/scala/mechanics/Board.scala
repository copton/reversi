package mechanics

import reversi.{Position,Occupation,Color}

trait Board {
  val size = 8
  def getOccupation(position: Position): Occupation
  protected def setOccupation(position: Position, occupation: Occupation): Unit

  def copyFrom(source: Board): Unit = {
    var x=0
    while(x < size) {
      var y=0
      while(y < size) {
        val pos = new Position(x, y)
        setOccupation(pos, getOccupation(pos))
        y+=1
      }
      x += 1
    }
  }

  override def toString(): String = {
    val s: StringBuffer = new StringBuffer
    for (x <- 0 to size-1) {
      for(y <- 0 to size-1) {
        s.append(getOccupation(new Position(x, y)) match {
          case Occupation.FREE => " "
          case Color.RED => "X"
          case Color.GREEN => "O"
        })
      }
      s.append("\n")
    }
    s.toString()
  }
}

trait ArrayBoard extends Board {
  val fields: Array[Array[Occupation]] = Array.fill(size, size)(Occupation.FREE)
  def getOccupation(pos: Position): Occupation = fields(pos.x)(pos.y)
  def setOccupation(pos: Position, occupation: Occupation): Unit = fields(pos.x).update(pos.y, occupation)
}


trait DefaultBoard {this: Board =>
  setOccupation(new Position(3,3), Color.GREEN)
  setOccupation(new Position(3,4), Color.RED)
  setOccupation(new Position(4,3), Color.RED)
  setOccupation(new Position(4,4), Color.GREEN) 
}
