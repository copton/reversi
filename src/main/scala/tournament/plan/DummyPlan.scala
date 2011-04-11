package tournament.plan

import akka.actor._
import scala.collection.immutable.List
import tournament.misc.GameResult
import tournament.misc.DummyGameResult
import tournament.misc.GameDetails
import tournament.misc.DummyGameDetails
import game._
import reversi.{Color, Position}


class DummyPlan extends Plan {

  var count: Int = 0

  def deliverResult(result: GameResult): Unit = {
	result match { case d: DummyGameResult => println("DummyGamePlan: the Winner is: " + d.winner) }
	
  }

  
  def requestGames: List[GameDetails] = {

	val red = Color.RED
	val green = Color.GREEN
	val player = "player.RandomPlayer"

	val players1 = List(player, player)
	val players2 = List(player, player)

	val colors1 = List(red, green)
	val colors2 = List(red, green)

	val gameDetails1 = new DummyGameDetails(players1, colors1)
	val gameDetails2 = new DummyGameDetails(players2, colors2)
	
	
	return List(gameDetails1, gameDetails2)
    	
	

  }

  def finished: Boolean = { 
	if(count < 2){
		count = count + 1
		return false
	} else {
		return true
	}
  }

  def getTournementInfo: TournementInfo = { null }

}
