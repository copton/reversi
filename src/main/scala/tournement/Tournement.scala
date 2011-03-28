package tournement

import se.scalablesolutions.akka.actor._
import se.scalablesolutions.akka.util.Logging
import se.scalablesolutions.akka.remote.RemoteServer
import game._
//import tournement.plan.Plan


class Tournement(plan: tournement.plan.Plan) extends Actor {
	
	var currentGames = Unit
	
	log.info("Tournement started")
	
	
	
	def receive = {
		/*
		case Start => 
			currentGames = plan.requestGames
			for(players <- currentGames) {
				var game = new Game()
				game ! StarGame(players)
			}
		
		case GameFinished(result: GameResult) => 
			plan.deliverResult(result)
			currentGames = plan.requestGames
			for(players <- currentGames) {
				var game = new Game()
				game ! StarGame(players)
			}
			
			
			
		case SomethingWentWrong(error: GameError) => println("somethingWentWrong")
		
    	case _ =>      println("received unknown message")
*/	
}

	

	
}


// for testing purposes

object RunTournement {
	def main(args: Array[String]) {
	  val gamePort = 10000	
    val gameServer = new RemoteServer
    gameServer.start("localhost", gamePort)

    
    val tournement = Actor.actorOf(new Tournement)
    gameServer.register("tournement", tournement)
	}
}
