package gameserver

import scala.collection.immutable.List


sealed trait Message

//from Tournament
case class RequestPorts(amount: Int) extends Message

case class ReleasePorts(portList: List[Int]) extends Message

//from starting mechanism
case class Start() extends Message

