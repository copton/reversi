package tournament.plan

import akka.actor._
import java.util.ArrayList
import scala.collection.immutable.List
import tournament.misc.GameResult
import tournament.misc.DummyGameResult
import tournament.misc.GameDetails
import tournament.misc.DummyGameOption
import game._
import reversi.{Color, Position}
import messages._


class DummyPlan extends Plan {


	var finishedGames: List[DummyGameResult] = Nil
	var count: Int = 0

	def deliverFinishedGame(game: GameResult): Unit = {
		finishedGames = game.asInstanceOf[DummyGameResult]::finishedGames
	
	}

  
	def requestGames: List[(GameDetails, _root_.game.gameCreation)] = {

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
		
		
		return List((gameDetails1, _root_.game.GameFactory), (gameDetails2, _root_.game.GameFactory)/*, (gameDetails3, _root_.game.GameFactory)*/)
    	
	}

	def finished: Boolean = { 
		if(count < 2){
			count = count + 1
			return false
		} else {
			return true
		}
	}

	def getTournamentInfo: TournamentInfo = { new DummyTournamentInfo }


}

class DummyTournamentInfo extends TournamentInfo {

	def getInfo: ArrayList[String] = {
		var result: ArrayList[String] = new ArrayList()
		result.add("this is just a Dummy")
		result.add("but here could be information about the state of a real tournament")
		return result
	}
}

