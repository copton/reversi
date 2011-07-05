package tournament

import akka.actor._
import akka.util.Logging
import java.util.LinkedList
import java.util.ArrayList
import scala.collection.immutable.List
import scala.collection.mutable.HashMap
import game._
import gameserver._
import tournament.plan.{DummyPlan, Plan}
import tournament.misc.{GameDetails, GameResult}
import reversi.Color
import messages._


class Tournament(val plan: Plan, val gameServer: ActorRef, remoteNode: akka.remoteinterface.RemoteServerModule, val name: String) extends Actor {
	
	var resourceAdmin: ActorRef = null	

	var status: String = "not yet started"
	var started: Boolean = false
	var gamesFinished = 0
	var tag: Int = 1
	
	var currentGames: List[GameDetails] = null
	var games: HashMap[String, (ActorRef, Boolean)] = new HashMap
	var readyGames: LinkedList[ActorRef] = new LinkedList()

	log.info("Tournament: Tournament started")


	override def preStart = {
		self.setId(name)
//		remoteNode.register(name, self)
		resourceAdmin = (Actor.registry.actorsFor("resourceAdministrator"))(0)
	}
	
	
	def receive = {

		case Start() => 
			log.info("received a Start-message")
			if(!started) {
				started = true
				status = "started"
				requestAndStartGames			
			}

		case Stop() =>
			log.info("received a Stop-message")
	
		
		case GameFinished(finishedGame: GameResult, portsToReturn: List[Int], uniqueTag: String) => 
			gamesFinished = gamesFinished + 1
			log.info("Tournament: we already finished " + gamesFinished + "games")
			plan.deliverFinishedGame(finishedGame)

			resourceAdmin ! _root_.messages.ReleasePorts(portsToReturn)
			resourceAdmin ! _root_.messages.ReleasePermission()
			requestAndStartGames

		case PermissionGranted() =>
			val nextOne = readyGames.poll()
			nextOne ! _root_.messages.StartGame()


			
		case WebLoadGameCollection() =>
			var result = new ArrayList[String]
			games foreach ( (t1) => result.add(t1._1))
			self.reply(result)

		case WebGetTournament() =>
			var result: TournamentReply = new TournamentReply(name, status, gamesFinished)
			self.reply(result)

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
					val playerPorts = (resourceAdmin !! _root_.messages.RequestPorts(2)).getOrElse(throw new RuntimeException("TIMEOUT")) //TODO asynchron
					log.info("Tournament: the playerPorts are: " + (playerPorts.asInstanceOf[List[Int]])(0) + " and " + (playerPorts.asInstanceOf[List[Int]])(1))
					val uniqueTag = tag; tag = tag + 1
					val gameName: String = name +"/game" + uniqueTag.toString()
					log.info("Tournament: the tag for this game is: " + uniqueTag.toString() + " and therefore, the gamename is: " + gameName)
					val game = GameFactory.createGame(playerPorts.asInstanceOf[List[Int]], currentGame, self, gameName)
					game.setId(gameName)
					remoteNode.register(gameName, game)
					games +="game"+uniqueTag.toString()-> (game, false)
					readyGames.offer(game)
					resourceAdmin ! _root_.messages.RequestPermission()

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



