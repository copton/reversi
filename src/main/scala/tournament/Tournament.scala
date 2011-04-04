package tournament

import akka.actor._
import akka.util.Logging
import scala.collection.immutable.List
import game._
import tournament.plan.{DummyPlan, Plan}
import tournament.misc.GameDetails
import reversi.Color


class Tournament(plan: Plan) extends Actor {
	
	var i = 0

	var currentGames: List[GameDetails] = null
	var gamePort = 10000
	
	log.info("Tournement started")


	
	
	
	def receive = {
		
		case Start2() =>
			println("asdf")		

		case Start() => 
			log.info("received a Start-message")
			currentGames = plan.requestGames
			for(currentGame <- currentGames) {
				for(player <- currentGame.players){
					println(player)

				}
				for(info <- currentGame.additionalInformation){
					info match {
						case color: Color	=> println(info.toString())
						case _			=> println("not recognized")
					}

				}
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
    		val plan = new DummyPlan
    		val tournament = Actor.actorOf(new Tournament(plan))
		tournament.start
		tournament ! Start()
	}


} 





