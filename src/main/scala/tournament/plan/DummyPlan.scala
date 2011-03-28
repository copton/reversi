package tournament.plan

import scala.collection.immutable.List

class DummyPlan extends Plan {

  def deliverResult(result: Object): Unit = {  }

  def requestGame: List[String] = {
	  return List("player.RandomPlayer","player.RandomPlayer")
  }
  
  def requestGames: List[List[String]] = {
	  return List (
	 		  		List("player.RandomPlayer","player.RandomPlayer"),
	 		  		List("player.RandomPlayer","player.RandomPlayer"),
	 		  		List("player.RandomPlayer","player.RandomPlayer")
	 		  	   )
  }

  def finished: Boolean = { false }

  def getTournementInfo: TournementInfo = { null }

}
