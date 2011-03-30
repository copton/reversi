package tournament.plan

import akka.actor._
import scala.collection.immutable.List
import tournament.misc.GameResult
import game._
import reversi.{Color, Position}


class DummyPlan extends Plan {

  var count: Int = 0
  var playerPort = 20001	

  def deliverResult(result: GameResult): Unit = {
	println(result.winner)
	
  }

  
  def requestGames: List[ActorRef] = {
    	

    	val playerRed = new Player("player.RandomPlayer", playerPort, Color.RED)
  	val playerGreen = new Player("player.RandomPlayer", playerPort + 1, Color.GREEN)
	playerPort = playerPort + 2
    	val game1 = Actor.actorOf(new Game(10000, Array(playerRed, playerGreen)))
	
//	val playerBlue = new Player("player.RandomPlayer", playerPort, Color.RED)
//  	val playerYellow = new Player("player.RandomPlayer", playerPort + 1, Color.GREEN)
//	playerPort = playerPort + 2
//    	val game2 = Actor.actorOf(new Game(10001, Array(playerBlue, playerYellow)))

	return List(game1/*, game2*/)
    	
	





//	  return List( (List("player.RandomPlayer","player.RandomPlayer"), new DummyGameDetails),
//		       (List("player.RandomPlayer","player.RandomPlayer"), new DummyGameDetails),
//		       (List("player.RandomPlayer","player.RandomPlayer"), new DummyGameDetails)
//			
//		 )
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
