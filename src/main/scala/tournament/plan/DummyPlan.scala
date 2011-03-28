package tournament.plan

import scala.collection.immutable.List
import tournament.misc.GameResult
import tournament.misc.GameDetails
import tournament.misc.DummyGameDetails

class DummyPlan extends Plan {

  var count: Int = 0

  def deliverResult(result: GameResult): Unit = {
	println(result.winner)
	
  }

  
  def requestGames: List[(List[String], GameDetails)] = {
	  return List( (List("player.RandomPlayer","player.RandomPlayer"), new DummyGameDetails),
		       (List("player.RandomPlayer","player.RandomPlayer"), new DummyGameDetails),
		       (List("player.RandomPlayer","player.RandomPlayer"), new DummyGameDetails)
			
		 )
  }

  def finished: Boolean = { 
	if(count == 0){
		count = count + 1
		return false
	} else {
		return true
	}
  }

  def getTournementInfo: TournementInfo = { null }

}
