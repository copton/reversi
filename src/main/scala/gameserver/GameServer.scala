package gameserver

import akka.actor._
import scala.collection.mutable.HashMap
import java.lang.Thread
import java.util.ArrayList
import java.util.Properties
import java.io.FileInputStream
import tournament._
import tournament.plan._
import resourceadministrator.ResourceAdministrator
import messages._

class GameServer extends Actor{
	var resourceAdministrator: ActorRef = null
	var fourZeroFourActor: ActorRef = null
	var tournaments: HashMap[String, (ActorRef, String)] = new HashMap
	var remoteNode: akka.remoteinterface.RemoteServerModule = null
	var remoteNodePort: Int = 9999

	val prop: Properties = new Properties();

	override def preStart() {
		val fis: FileInputStream = new FileInputStream("settings.xml");
		prop.loadFromXML(fis)
		remoteNodePort = prop.getProperty("basePort").toInt
		val maxGames: Int = prop.getProperty("maxGames").toInt
		resourceAdministrator = createResourceAdministrator(remoteNodePort + 1, maxGames)
		log.info("GameServer: resourceAdministrator started")
		fourZeroFourActor = Actor.actorOf(new FourZeroFourActor); fourZeroFourActor.start
		remoteNode = Actor.remote.start("localhost", remoteNodePort)
		
	}


	def receive = {
		case ServerStart() =>
			startTournaments(remoteNode)
	
		case WebTest() =>
			self.reply("Webtest seems to work")

		case WebGetRoot() =>
			var reply = new _root_.messages.RootReply
			self.reply(reply)

		case WebLoadTournamentCollection() =>
			var result = new ArrayList[String]
			tournaments foreach ( (t1) => result.add(t1._1) )
			self.reply(result)

		case WebGetTournaments() =>
			val result = new _root_.messages.TournamentsReply(tournaments.size.toString)
			self.reply(result)

		case WebRequestActor(actorName: String) =>
			try {
				self.reply((Actor.registry.actorsFor(actorName))(0))
			} catch {
				case e => self.reply(fourZeroFourActor)
			}

		case WebPostForTournament(tournamentName: String, postArgument: String) =>
			val target: ActorRef = (Actor.registry.actorsFor("/tournaments/" + tournamentName))(0)

			postArgument match {
				case "start" =>
					(target !! _root_.messages.Start()).getOrElse(throw new RuntimeException("TIMEOUT"))
					self.reply("OK..")
				case "delete" =>
					val targetPlanName = tournaments(tournamentName)._2
					val planObject = this.getClass.getClassLoader.loadClass(targetPlanName)
					val plan = planObject.newInstance
					(target !! _root_.messages.Stop(plan.asInstanceOf[Plan])).getOrElse(throw new RuntimeException("TIMEOUT"))

					
					self.reply("OK..")
			}
			


		case msg => 
			println("unknown message received: " + Some(self.sender.get).toString + msg.toString)
			val reply: String = "<a href='http://akka.io'>Akka Actors rock!</a>"
			self.reply(reply)
			
	}


	/**
	* Starts all Tournament that have an entry in the settings.xml
	*/
	def startTournaments(remoteNode: akka.remoteinterface.RemoteServerModule): Unit = {
		val planNames: Array[String] = prop.getProperty("reversiplans").split(',')
		planNames.foreach{ planName:String =>
			println("name of the plan: " + planName)
			val planObject = this.getClass.getClassLoader.loadClass(planName)
			val plan = planObject.newInstance
			val tournamentName = (resourceAdministrator !! _root_.messages.RequestTournamentName()).getOrElse(throw new RuntimeException("TIMEOUT")).asInstanceOf[String]
			val tournament = Actor.actorOf(new Tournament(plan.asInstanceOf[Plan], self, remoteNode, "/tournaments/"+tournamentName.toString()))
			log.info("GameServer: created a new tournament with the name " + tournamentName)
			tournaments += tournamentName.asInstanceOf[String] -> (tournament, planName)
			tournament.start
		}

	}

	
	def createResourceAdministrator(basePort: Int, maxGames: Int): ActorRef = {
		var resourceAdministrator = Actor.actorOf(new ResourceAdministrator(basePort, maxGames))
		resourceAdministrator.setId("resourceAdministrator")
		resourceAdministrator.start
		return resourceAdministrator
	}


}


object RunGameServer {
	def main(args: Array[String]) {

		val gameServer = Actor.actorOf(new GameServer)
		gameServer.start

		val resourceAdmin = (Actor.registry.actorsFor("resourceAdministrator"))(0)
		val gamePort = (resourceAdmin !! _root_.messages.RequestPorts(1)).getOrElse(throw new RuntimeException("RunGameServer: TIMEOUT"))
		println("to test the resourceAdministrator: the port I requested is: " + (gamePort match {case l: List[Int] => l(0) }).toString  )
		
		var WebServer = new WebServerThread(gameServer)
		WebServer.start()

		gameServer ! _root_.messages.ServerStart()
	}
}

/**
* The webserver should run in its own thread
*/
class WebServerThread(var gameServer: ActorRef) extends Thread {

	var webServer = new _root_.ch.ethz.inf.vs.projectname.JerseyMain()

	override def run() = webServer.myServerStarter(gameServer)
}


/*
* Attempt to handle requests for not-existing ressources. Just a workaround.
*/
class FourZeroFourActor extends Actor {
	def receive = {


		case WebLoadTournamentCollection() =>
			var result = new ArrayList[String]
			result.add("404. Resource does not exist")
			self.reply(result)
		case WebLoadGameCollection() =>
			var result = new ArrayList[String]
			result.add("404. Resource does not exist")
			self.reply(result)
		case WebLoadTurnCollection() =>
			var result = new ArrayList[String]
			result.add("404. Resource does not exist")
			self.reply(result)
		case WebLoadPlayerCollection() =>
			var result = new ArrayList[String]
			result.add("404. Resource does not exist")
			self.reply(result)

		case _ => 
			val result = new _root_.messages.FourZeroFourReply
			self.reply(result)
	}
	
}

 
