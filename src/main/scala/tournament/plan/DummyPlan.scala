package tournament.plan

import akka.actor._
import scala.collection.immutable.List
import tournament.misc.GameResult
import tournament.misc.DummyGameResult
import tournament.misc.GameDetails
import tournament.misc.DummyGameOption
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
		val players3 = List(player, player)
	
		val colors1 = List(red, green)
		val colors2 = List(red, green)
		val colors3 = List(red, green)
	
		val option1 = new DummyGameOption(colors1)
		val option2 = new DummyGameOption(colors2)
		val option3 = new DummyGameOption(colors3)
	
		val gameDetails1 = new GameDetails(players1, List(option1))
		val gameDetails2 = new GameDetails(players2, List(option2))
		val gameDetails3 = new GameDetails(players3, List(option3))
		
		
		return List(gameDetails1, gameDetails2/*, gameDetails3*/)
    	
	}

	def finished: Boolean = { 
		if(count < 10){
			count = count + 1
			return false
		} else {
			return true
		}
	}

	def getTournamentInfo: TournamentInfo = { null }

	def getGameInfo(gameId: String): GameInfo = { null }

}
