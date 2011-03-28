package tournament.plan

import tournament.misc.GameResult
import tournament.misc.GameDetails

trait Plan {

	/**
	 * delivers the result of a finished game to the plan.
	 */	
	def deliverResult(result: GameResult): Unit

	
	/**
	 * requests new games which should be run according to the tournement plan
	 * returns a list of tuples containing a list of playerids and GameDetails. Additional information might be stored there.
	 */
	def requestGames: List[(List[String], GameDetails)]

	/**
	 * Returns if the tournement is finished
	 */	
	def finished: Boolean


	/**
	 * returns the structure an status of the tournement. Mainly to answer the 'GET /reversi/tournement/' request.
	 * 
	 */	
	def getTournementInfo: TournementInfo
		
	
	

}
