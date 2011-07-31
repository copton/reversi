package tournament.plan

import akka.actor.ActorRef
import java.util.ArrayList
import scala.collection.immutable.List
import tournament.misc.GameResult
import tournament.misc.GameDetails

trait Plan {

	/**
	 * delivers the ActorRef of the finished game to the plan.
	 **/	
	def deliverFinishedGame(game: GameResult): Unit

	
	/**
	 * requests new games which should be run according to the tournament plan
	 * 
	 * returns needed details for every game, so that an according game can be created.
	 * Also returns the right GameFactory, so that the tournament can create the right games.
	 * List may be empty if there are no games to be run right now. (i.e. other games needs to be finished before a new tier of games is available)
	 **/
	def requestGames: List[(GameDetails, _root_.game.gameCreation)]

	/**
	 * Returns true if the tournament is finished
	 **/	
	def finished: Boolean


	/**
	 * returns information about this tournament. Mainly to answer the 'GET /reversi/tournament/' request.
	 * 
	 **/	
	def getTournamentInfo: TournamentInfo

}

trait TournamentInfo {

	def getInfo: ArrayList[String]

}

