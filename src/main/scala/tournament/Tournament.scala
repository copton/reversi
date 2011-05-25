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


class Tournament(val plan: Plan, val gameServer: ActorRef, remoteNode: akka.remoteinterface.RemoteServerModule, val name: String) extends Actor {
	

	var gamesFinished = 0

	var currentGames: List[GameDetails] = null
	var games: HashMap[String, (ActorRef, Boolean)] = new HashMap

	log.info("Tournament: Tournament started")


	override def preStart = {
		remoteNode.register(name, self)
	}
	
	
	def receive = {

		case Start() => 
			log.info("received a Start-message")
			requestAndStartGames			
		
	
		
		case GameFinished(finishedGame: ActorRef, portsToReturn: List[Int], uniqueTag: String) => 
			gamesFinished = gamesFinished + 1
			log.info("Tournament: we already finished " + gamesFinished + "games")
			plan.deliverFinishedGame(finishedGame)

			gameServer ! _root_.messages.ReleasePorts(portsToReturn)
			requestAndStartGames
			
		case WebLoadGameCollection() =>
			var result: String = ""
			games foreach ( (t1) => result = result + t1._1 + "\n")
			self.reply(result)

		case WebGetTournament() =>
			self.reply("Amount of finished games: " + gamesFinished.toString() + "\n are we finished?: " + plan.finished.toString())

  	  	case _ =>
			println(name +  ": received unknown message")
			self.reply("OK")

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
					val gameName: String = name +"/game" + uniqueTag.toString()
					log.info("Tournament: the tag for this game is: " + uniqueTag.toString() + " and therefore, the gamename is: " + gameName)
					val game = GameFactory.createGame(playerPorts.asInstanceOf[List[Int]], currentGame, self, gameName)
	  				remoteNode.register(gameName, game)
					games +="game"+uniqueTag.toString()-> (game, false)
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



