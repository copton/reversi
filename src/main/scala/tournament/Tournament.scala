package tournament

import akka.actor._
import akka.util.Logging
import scala.collection.immutable.List
import game._
import tournament.plan.{DummyPlan, Plan}


class Tournament(plan: Plan) extends Actor {
	
	var i = 0

	var currentGames: List[akka.actor.ActorRef] = null
	var gamePort = 10000
	
	log.info("Tournement started")

//	val gameServer = Actor.remote.start("localhost", gamePort)
	
	
	
	def receive = {
		
		case Start2() =>
//			val gamePort = 10000	
    			val gameServer = Actor.remote.start("localhost", gamePort)

	    		val playerRed = new Player("player.RandomPlayer", gamePort + 1, reversi.Color.RED)
	    		val playerGreen = new Player("player.RandomPlayer", gamePort + 2, reversi.Color.GREEN)
	    		val game = Actor.actorOf(new Game(gamePort, Array(playerRed, playerGreen)))
	    		gameServer.register("game", game)			

		case Start() => 
			log.info("received a Start-message")
			currentGames = plan.requestGames
			for(currentGame <- currentGames) {
//				gameServer.register("asdf" + i, currentGame)
				val gameServer = Actor.remote.start("localhost", gamePort/*+i*/)
				gameServer.register("currentGame"/* + i*/, currentGame)
				i = i + 1
			}
		
//		case GameFinished(result: GameResult) => 
//			plan.deliverResult(result)
//			currentGames = plan.requestGames
//			for(players <- currentGames) {
//				var game = new Game()
//				game ! StarGame(players)
//			}
			
			
			
//		case SomethingWentWrong(error: GameError) => println("somethingWentWrong")
		
  	  	case _ =>      println("received unknown message")

}

	

	
}

case class Start()
case class Start2()

// for testing purposes

object RunTournament {
	def main(args: Array[String]) {
//	  	val tournamentPort = 9999
//  		val tournamentServer = Actor.remote.start("localhost", tournamentPort)
    		val plan = new DummyPlan
    		val tournament = Actor.actorOf(new Tournament(plan))
		tournament.start
//    		tournamentServer.register("tournament", tournament)
		tournament ! Start2()
	}

//	def main(args: Array[String]) {
//		val gamePort = 10000	
//    		val gameServer = Actor.remote.start("localhost", gamePort)
//
//    		val playerRed = new Player("player.RandomPlayer", gamePort + 1, reversi.Color.RED)
//    		val playerGreen = new Player("player.RandomPlayer", gamePort + 2, reversi.Color.GREEN)
//    		val game = Actor.actorOf(new Game(gamePort, Array(playerRed, playerGreen)))
//    		gameServer.register("game", game)
//	}

} 





