package tournament.plan

trait Plan {
	
	//def deliverResult(result: GameResult): Unit
		//delivers the result of a finished game to the plan.
	
	def requestGame: List[String]
	    //requests a new game that should be run according to the tournement plan
	    //returns the playerids.
	
	def requestGames: List[List[String]]
	
	def finished: Boolean
		//true, if the tournement is decided, false if not
	
	def getTournementInfo: TournementInfo
		//returns the structure an status of the tournement. Mainly to answer the 'GET /reversi/tournement/' request.
	
	

}
