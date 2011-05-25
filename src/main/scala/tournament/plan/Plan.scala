package tournament.plan

import akka.actor.ActorRef
import scala.collection.immutable.List
import tournament.misc.GameResult
import tournament.misc.GameDetails

trait Plan {

	/**
	 * delivers the ActorRef of the finished game.
	 **/	
	def deliverFinishedGame(game: ActorRef): Unit

	
	/**
	 * requests new games which should be run according to the tournament plan
	 * 
	 * returns needed details for every game, so that an according game can be created
	 * List may be empty if there are no games to be run right now. (i.e. other games needs to be finished before a new tier of games is available)
	 **/
	def requestGames: List[GameDetails]

	/**
	 * Returns true if the tournament is finished
	 **/	
	def finished: Boolean


	/**
	 * returns information about this tournament. Mainly to answer the 'GET /reversi/tournement/' request.
	 * 
	 **/	
	def getTournamentInfo: TournamentInfo

	/**
	 * returns information about a particular game. 
	 * 
	 **/	
	def getGameInfo(gameId: String): GameInfo
	

}

trait GameInfo
trait TournamentInfo
trait TurnInfo
