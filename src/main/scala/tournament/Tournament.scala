package tournament

import akka.actor._
import akka.util.Logging
import scala.collection.immutable.List
import scala.collection.mutable.HashMap
import game._
import gameserver._
import tournament.plan.{DummyPlan, Plan}
import tournament.misc.{GameDetails, GameResult}
import reversi.Color
import messages._


class Tournament(val plan: Plan, val gameServer: ActorRef, remoteNode: akka.remoteinterface.RemoteServerModule) extends Actor {
	
	var nameCounter: Int = 1

	var gamesFinished = 0

	var currentGames: List[GameDetails] = null
	var games: HashMap[String, (ActorRef, Boolean)] = new HashMap
	log.info("Tournament: Tournament started")


	
	
	
	def receive = {

		case Start() => 
			log.info("received a Start-message")
			requestAndStartGames			
	
		
		case GameFinished(result: GameResult, finishedGame: ActorRef, portsToReturn: List[Int], uniqueTag: Int) => 
			gamesFinished = gamesFinished + 1
			log.info("Tournament: we already finished " + gamesFinished + "games")
			plan.deliverResult(result)

			remoteNode.unregister(uniqueTag.toString())
			gameServer ! _root_.messages.ReleasePorts(portsToReturn)
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
					log.info("Tournament: creating a new game:")
					val playerPorts = (gameServer !! _root_.messages.RequestPorts(2)).getOrElse(throw new RuntimeException("TIMEOUT"))
					log.info("Tournament: the playerPorts are: " + (playerPorts.asInstanceOf[List[Int]])(0) + " and " + (playerPorts.asInstanceOf[List[Int]])(1))
					val uniqueTag = (gameServer !! RequestTag()).getOrElse(throw new RuntimeException("TIMEOUT"))
					log.info("Tournament: the tag for this game is: " + uniqueTag.toString())
				
					val game = GameFactory.createGame(playerPorts.asInstanceOf[List[Int]], currentGame, self, uniqueTag.asInstanceOf[Int])
	  				remoteNode.register(uniqueTag.toString(), game)
					games +=  "game"+nameCounter.toString()-> (game, false)
					nameCounter = nameCounter + 1
					game ! _root_.messages.StartGame()


				}//end for
			}//end if
		}//end else
	}//end def	

/////////////////////// Rest Connection Stuff

	def getGames: Unit = {

	}

	def getGame(gameIdentifier: String): Unit = {

	}

/////////////////////////////////////	
}



