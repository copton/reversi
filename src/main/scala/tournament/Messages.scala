package tournament

import scala.collection.immutable.List

sealed trait Message

//from Tournament to Server

case class RequestPorts(amount: Int) extends Message

case class ReleasePorts(portList: List[Int]) extends Message

//from Server to Tournament

case class PortDelivery(requestedPorts: List[Int]) extends Message

case class Start() extends Message
