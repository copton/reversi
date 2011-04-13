package tournament

import akka.actor._
import akka.util.Logging
import scala.collection.immutable.List
import game._
import tournament.plan.{DummyPlan, Plan}
import tournament.misc.{GameDetails, DummyGameDetails, GameResult, DummyGameResult}
import reversi.Color


class Tournament(plan: Plan, gameServer: ActorRef) extends Actor {
	
	var i = 0

	var gamesStarted = 0

	var currentGames: List[GameDetails] = null
	var serverPort: List[Int] = null//(gameServer !! RequestPorts(1)).getOrElse(throw new RuntimeException("Tournament: TIMEOUT"))
	var testServer: akka.remoteinterface.RemoteServerModule = null //Actor.remote.start("localhost", (serverPort.asInstanceOf[List[Int]])(0))
	var uniqueNumber = 1
	log.info("Tournement started")


	
	
	
	def receive = {

		case Start() => 
			log.info("received a Start-message")
			serverPort = (gameServer !! RequestPorts(1)).getOrElse(throw new RuntimeException("Tournament: TIMEOUT")).asInstanceOf[List[Int]]
			testServer = Actor.remote.start("localhost", (serverPort.asInstanceOf[List[Int]])(0))
			requestAndStartGames			
	
		
		case GameFinished(result: GameResult, finishedGame: ActorRef, portsToReturn: List[Int]) => 
			plan.deliverResult(result)
			finishedGame.stop
//			gameServer ! ReleasePorts(portsToReturn)
			requestAndStartGames
			
			
			
			
//		case SomethingWentWrong(error: GameError) => println("somethingWentWrong")
		
  	  	case _ =>      println("received unknown message")

	}	

	def requestAndStartGames: Unit = {
		if (plan.finished) {
			log.info("Tournament: keine games mehr -> Später wird das dem GameServer weitergeleitet")
		} else {
			currentGames = plan.requestGames
			if(!currentGames.isEmpty){

				for(currentGame <- currentGames) {
					gamesStarted = gamesStarted + 1
					log.info("now we already started " + gamesStarted + " games")
					val playerPorts = (gameServer !! RequestPorts(2)).getOrElse(throw new RuntimeException("TIMEOUT"))
					log.info("Tournament: the playerPorts are: " + (playerPorts.asInstanceOf[List[Int]])(0) + " and " + (playerPorts.asInstanceOf[List[Int]])(1))
				
					val game = GameFactory.createGame((serverPort.asInstanceOf[List[Int]])(0), playerPorts.asInstanceOf[List[Int]], currentGame, self, uniqueNumber)
	  				testServer.register("game"+uniqueNumber, game)
					uniqueNumber = uniqueNumber + 1
//					game ! StartGame()


				}//end for
			}//end if
		}//end else
	}//end def	

	
}


// for testing purposes

object RunTournament {
	def main(args: Array[String]) {
  		val plan = new DummyPlan
    		val tournament = Actor.actorOf(new Tournament(plan, null))
		tournament.start
		tournament ! Start()
	}


} 





