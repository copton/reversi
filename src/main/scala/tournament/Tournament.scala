package tournament

import akka.actor._
import akka.util.Logging
import scala.collection.immutable.List
import game._
import gameserver._
import tournament.plan.{DummyPlan, Plan}
import tournament.misc.{GameDetails, GameResult}
import reversi.Color


class Tournament(plan: Plan, gameServer: ActorRef) extends Actor {
	
	var i = 0

	var gamesFinished = 0

	var currentGames: List[GameDetails] = null
	var serverPort: List[Int] = null
	var testServer: akka.remoteinterface.RemoteServerModule = null
	var uniqueNumber = 1
	log.info("Tournement started")


	
	
	
	def receive = {

		case Start() => 
			log.info("received a Start-message")
			serverPort = (gameServer !! RequestPorts(1)).getOrElse(throw new RuntimeException("Tournament: TIMEOUT")).asInstanceOf[List[Int]]
			testServer = Actor.remote.start("localhost", (serverPort.asInstanceOf[List[Int]])(0))
			requestAndStartGames			
	
		
		case GameFinished(result: GameResult, finishedGame: ActorRef, portsToReturn: List[Int], namingNumber: Int) => 
			gamesFinished = gamesFinished + 1
			log.info("Tournament: we already finished " + gamesFinished + "games")
			plan.deliverResult(result)
//			finishedGame ! KillAll()
//			testServer.unregister("game"+namingNumber)

//			gameServer ! ReleasePorts(portsToReturn)
			requestAndStartGames
			
		
  	  	case _ =>      println("received unknown message")

	}	

	def requestAndStartGames: Unit = {
		if (plan.finished) {
			log.info("Tournament: keine games mehr -> Später wird das dem GameServer weitergeleitet")
			
		} else {
			currentGames = plan.requestGames
			if(!currentGames.isEmpty){

				for(currentGame <- currentGames) {
					val playerPorts = (gameServer !! RequestPorts(2)).getOrElse(throw new RuntimeException("TIMEOUT"))
					log.info("Tournament: the playerPorts are: " + (playerPorts.asInstanceOf[List[Int]])(0) + " and " + (playerPorts.asInstanceOf[List[Int]])(1))
				
					val game = GameFactory.createGame((serverPort.asInstanceOf[List[Int]])(0), playerPorts.asInstanceOf[List[Int]], currentGame, self, uniqueNumber)
	  				testServer.register("game"+uniqueNumber, game)
					uniqueNumber = uniqueNumber + 1
					game ! StartGame()


				}//end for
			}//end if
		}//end else
	}//end def	

	
}



