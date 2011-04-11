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

	var currentGames: List[GameDetails] = null
	var testPort = 10400
	log.info("Tournement started")


	
	
	
	def receive = {

		case Start() => 
			log.info("received a Start-message")
			requestAndStartGames			
	
		
		case GameFinished(result: GameResult, game: ActorRef) => 
			plan.deliverResult(result)
			game.stop
			requestAndStartGames
			
			
			
			
//		case SomethingWentWrong(error: GameError) => println("somethingWentWrong")
		
  	  	case _ =>      println("received unknown message")

	}	

	def requestAndStartGames: Unit = {
		if (plan.finished) {
			log.info("Tournament: keine games mehr -> Später wird das dem GameServer weitergeleitet")
		} else {
			currentGames = plan.requestGames
			for(currentGame <- currentGames) {
//				val gamePort = gameServer !! RequestPorts(1)
//				val playerPorts = gameServer !! RequestPorts(2)
				val gamePort = testPort
				log.info("gamePort = " + testPort)
				testPort = testPort + 1
				val playerPort1 = testPort
				log.info("playerPort1 = " + testPort)
				testPort = testPort + 1
				val playerPort2 = testPort
				log.info("playerPort2 = " + testPort)
				testPort = testPort + 1
				val playerPorts = List(playerPort1, playerPort2)
				val game = GameFactory.createGame(gamePort, playerPorts, currentGame, self)
				var testServer = Actor.remote.start("localhost", gamePort)
    				testServer.register("game"+gamePort, game)
//				game ! StartGame()


			}//end for
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





