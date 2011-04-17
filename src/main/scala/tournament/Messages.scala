package tournament

import akka.actor._
import scala.collection.immutable.List
import tournament.misc.{GameResult}


sealed trait Message


//from Server

case class Start() extends Message

// from Game

case class GameFinished(result: GameResult, game: ActorRef, portsToRelease: List[Int], namingNumber: Int) extends Message

