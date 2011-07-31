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


class Tournament(var plan: Plan, val gameServer: ActorRef, remoteNode: akka.remoteinterface.RemoteServerModule, val name: String) extends Actor {
	
	var resourceAdmin: ActorRef = null	

	var status: String = "not yet started"
	var started: Boolean = false
	var gamesFinished = 0
	var tag: Int = 1
	
	var currentGames: List[(GameDetails, _root_.game.gameCreation)] = null
	var games: HashMap[String, (ActorRef, Boolean)] = new HashMap
	var readyGames: LinkedList[ActorRef] = new LinkedList()

	log.info("Tournament: Tournament started")


	override def preStart = {
		self.setId(name)
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
			self.reply("OK..")

		case Stop(plan: _root_.tournament.plan.Plan) =>
			log.info("received a Stop-message")
			if(started){
				status = "Tournament resetted. Press Start to restart."
				started = false
				destroyOldStuff
				createNewStuff(plan)
			}
			self.reply("OK..")
	
		
		case GameFinished(finishedGame: GameResult, portsToReturn: List[Int], uniqueTag: String) => 
			gamesFinished = gamesFinished + 1
			log.info("Tournament: we already finished " + gamesFinished + "games")
			plan.deliverFinishedGame(finishedGame)

			resourceAdmin ! _root_.messages.ReleasePorts(portsToReturn)
			resourceAdmin ! _root_.messages.ReleasePermission()
			requestAndStartGames

		case EmergencyFinished(portsToReturn: List[Int]) =>
				resourceAdmin ! _root_.messages.ReleasePorts(portsToReturn)
				resourceAdmin ! _root_.messages.ReleasePermission()	
			

		case PermissionGranted() =>
			val nextOne = readyGames.poll()
			nextOne ! _root_.messages.StartGame()


			
		case WebLoadGameCollection() =>
			var result = new ArrayList[String]
			games foreach ( (t1) => result.add(t1._1))
			self.reply(result)

		case WebGetTournament() =>
			var tournamentInfo = plan.getTournamentInfo
			var result: TournamentReply = new TournamentReply(name, status, gamesFinished, tournamentInfo)
			self.reply(result)

  	  	case msg =>
			println(name +  ": received unknown message: " + Some(self.sender.get).toString + msg.toString)
			self.reply("OK")

	}

	def destroyOldStuff: Unit = {
		games foreach ( (t2) => t2._2._1 ! _root_.messages.EmergencyExit() )
		gamesFinished = 0
		tag = 1
	
		currentGames = null
		games = new HashMap
		readyGames = new LinkedList()
		plan = null

	
	}

	def createNewStuff(plan: _root_.tournament.plan.Plan): Unit = {
		this.plan = plan
	}	

	def requestAndStartGames: Unit = {
		if (plan.finished) {
			log.info("Tournament: No more games to run. This Tournament is finished.")
			
		} else {
			currentGames = plan.requestGames
			if(!currentGames.isEmpty){

				for(currentGame <- currentGames) {
					log.info("Tournament: creating a new game:")
					val playerPorts = (resourceAdmin !! _root_.messages.RequestPorts(2)).getOrElse(throw new RuntimeException("TIMEOUT"))
					log.info("Tournament: the playerPorts are: " + (playerPorts.asInstanceOf[List[Int]])(0) + " and " + (playerPorts.asInstanceOf[List[Int]])(1))
					val uniqueTag = tag; tag = tag + 1
					val gameName: String = name +"/game" + uniqueTag.toString()
					log.info("Tournament: the tag for this game is: " + uniqueTag.toString() + " and therefore, the gamename is: " + gameName)
					val gameFactory = currentGame._2
					val game = gameFactory.createGame(playerPorts.asInstanceOf[List[Int]], currentGame._1, self, gameName)
					game.setId(gameName)
					remoteNode.register(gameName, game)
					games +="game"+uniqueTag.toString()-> (game, false)
					readyGames.offer(game)
					resourceAdmin ! _root_.messages.RequestPermission()

				}//end for
			}//end if
		}//end else
	}//end def	


}

