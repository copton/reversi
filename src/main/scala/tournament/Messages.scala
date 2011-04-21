package tournament

import akka.actor._
import scala.collection.immutable.List
import tournament.misc.{GameResult}
import tournament.plan.TurnInfo


sealed trait Message


//from Server

case class Start() extends Message

// from Game

case class GameFinished(result: GameResult, game: ActorRef, portsToRelease: List[Int], namingNumber: Int) extends Message

// Information about a requested turn
case class ReturnTurnInformation(info: TurnInfo) extends Message

