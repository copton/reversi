package tournament

import akka.actor._
import akka.util.Logging
//import akka.remote.RemoteServer
import game._
import tournament.plan.Plan


class Tournement(plan: Plan) extends Actor {
	
	var currentGames = Unit
	
	log.info("Tournement started")
	
	
	
	def receive = {
		/*
		case Start => 
			currentGames = plan.requestGames
			for(currentGame <- currentGames) {
				
			}
		
		case GameFinished(result: GameResult) => 
			plan.deliverResult(result)
			currentGames = plan.requestGames
			for(players <- currentGames) {
				var game = new Game()
				game ! StarGame(players)
			}
			
			
			
		case SomethingWentWrong(error: GameError) => println("somethingWentWrong")
		*/
    	case _ =>      println("received unknown message")

}

	

	
}

case class Start()

// for testing purposes
/*
object RunTournement {
	def main(args: Array[String]) {
	  val gamePort = 10000	
    val gameServer = new RemoteServer
    gameServer.start("localhost", gamePort)

    
    val tournement = Actor.actorOf(new Tournement)
    gameServer.register("tournement", tournement)
	}
} */
