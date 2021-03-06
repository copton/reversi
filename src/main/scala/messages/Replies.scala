package messages

import java.util.HashMap
import reversi.{Position, Occupation, Color}
import game.Player

abstract class BaseReply extends Renderer

class FourZeroFourReply extends BaseReply {
	def renderHtml(): String = {
		var result: String = "404. Resource does not exist"
		return result		
	}

	 def renderJson(): String = { null }

	 def renderXml(): String = {null}
}


class RootReply extends BaseReply {

	 def renderHtml(): String = {
		var result: String = "Welcome to Tournament Server"
		return result		
	}

	 def renderJson(): String = { null }

	 def renderXml(): String = {null}

}

class GameReply(val winner: String) extends BaseReply {
	 def renderHtml(): String = {
		var result: String = "<div>" + winner + "</div>"
		return result		
	}

	 def renderJson(): String = { null }

	 def renderXml(): String = {null}

}

class TournamentsReply(val amountOfTournaments: String) extends BaseReply {

	 def renderHtml(): String = {
		var result: String = "Tournaments on the Server: " + amountOfTournaments
		return result		
	}

	 def renderJson(): String = { null }

	 def renderXml(): String = {
		val s: StringBuffer = new StringBuffer
		s.append("<tournaments>\n")
		"adsf"
	}

}





class TournamentReply(var tournamentName: String, var status: String, var amountOfFinishedGames: Int, tournamentInfo: _root_.tournament.plan.TournamentInfo) extends BaseReply {

	 def renderHtml(): String = {
		val s: StringBuffer = new StringBuffer
		s.append("<div>\n")
		s.append("<table>\n")
		
		s.append("<tr>\n")
		s.append("<td>\n"); s.append("Name"); s.append("</td>\n")
		s.append("<td>\n"); s.append(tournamentName); s.append("</td>\n")
		s.append("</tr>\n")

		s.append("<tr>\n")
		s.append("<td>\n"); s.append("Status"); s.append("</td>\n")
		s.append("<td>\n"); s.append(status); s.append("</td>\n")
		s.append("</tr>\n")

		s.append("<tr>\n")
		s.append("<td>\n"); s.append("No. of Games finished"); s.append("</td>\n")
		s.append("<td>\n"); s.append(amountOfFinishedGames.toString); s.append("</td>\n")
		s.append("</tr>\n")
		
		s.append("</table></div>\n")

		var iterator = tournamentInfo.getInfo.iterator()
		s.append("<ul>\n")
		while(iterator.hasNext()) {
			s.append("<li>" + iterator.next().toString + "</li>\n")
		}
		s.append("</ul>\n")	
		
		s.append("<div>\n")
		s.append("<form name=\"input\" action=\"{{RessourceUri}}\" method=\"post\">\n<input class=\"posterSubmit\" value=\"Start\" type=\"submit\"/>\n<input type=\"hidden\" value = \"start\" name = \"value\"/>\n</form>\n")
		s.append("<form name=\"input\" action=\"{{RessourceUri}}\" method=\"post\">\n<input class=\"posterSubmit\" value=\"Reset\" type=\"submit\"/>\n <input type=\"hidden\" value = \"delete\" name = \"value\"/>\n</form>\n")
		s.append("</div>\n")


		return s.toString		
	}

	 def renderJson(): String = { null }

	 def renderXml(): String = {null}

}

class TurnReply(val turnNumber: Int, val nextExists: Boolean, val previousExists: Boolean, val gameName: String, var playField: _root_.game.GameboardSnapshot) extends BaseReply with boardDrawer {

	 def renderHtml(): String = {

		val s: StringBuffer = new StringBuffer
		if(previousExists) {
			s.append("<a href=\"" + gameName + "/turns/turn" + (turnNumber-1).toString + "\"> previous Turn</a>")
		}
		if(nextExists) {
			s.append("<a href=\"" + gameName + "/turns/turn" + (turnNumber+1).toString + "\"> next Turn</a>\n")
		}
		s.append("<br/>\n")
		s.append(drawHtmlBoard(playField.playField, playField.redStones.toString, playField.greenStones.toString))
		return s.toString
	}

	 def renderJson(): String = { null }

	 def renderXml(): String = {null}
}

class CurrentTurnReply(var snapshot: _root_.game.GameboardSnapshot) extends BaseReply with boardDrawer {


	 def renderHtml(): String = {

		val s: StringBuffer = new StringBuffer
		s.append("<h1 id=\"turnName\">" + snapshot.turn + "</h1>\n")
		s.append(drawHtmlBoard(snapshot.playField, snapshot.redStones.toString, snapshot.greenStones.toString))
		return s.append("<input type=\"button\" id=\"Button2\" value=\"Reset Playfield\" onclick=\"ResetCookie()\" />").toString
	}

	 def renderJson(): String = { null }

	 def renderXml(): String = {null}

}

class PlayerReply(val log: java.util.ArrayList[String]) extends BaseReply {

	 def renderHtml(): String = {
		val s: StringBuffer = new StringBuffer
		var iterator = log.iterator()
		
		s.append("<ul>\n")
		while(iterator.hasNext()) {
			s.append("<li>" + iterator.next().toString + "</li>\n")
		}

		s.append("</ul>\n")
		return s.toString()

	}

	 def renderJson(): String = { null }

	 def renderXml(): String = {null}

}


trait boardDrawer {

	def drawHtmlBoard(playField: Array[Array[String]], redCount: String, greenCount: String): String = {
		val red: String = "/webresources/images/32x32/red.gif"
		val green: String = "/webresources/images/32x32/green.gif"
		val redmark: String = "/webresources/images/32x32/redmark.gif"
		val greenmark: String = "/webresources/images/32x32/greenmark.gif"
		val nothing: String = "/webresources/images/32x32/nothing.png"
		val possible: String= "/webresources/images/32x32/possibleMove.png"


		val s: StringBuffer = new StringBuffer
		s.append("<table>\n")
		s.append("<tr>\n")
		s.append("<td><img src=\"/webresources/images/32x32/red.gif\"/></td><td>" + redCount + "</td>\n")
		s.append("<td><img src=\"/webresources/images/32x32/green.gif\"/></td><td>" + greenCount + "</td>\n")
		s.append("</tr>\n")
		s.append("</table>\n")

		s.append("<table id=\"playfield\">\n")
		
		for (i <- 0 to 7) {
			s.append("<tr>\n")
			for(j <- 0 to 7) {
				s.append("<td>")
				playField(i)(j) match {
					case "red" => s.append("<img src=\"" + red +"\" width=\"40\" height=\"40\" alt=\"red\" />")
					case "green" => s.append("<img src=\"" + green +"\" width=\"40\" height=\"40\" alt=\"red\" />")
					case "nothing" => s.append("<img src=\"" + nothing +"\" width=\"40\" height=\"40\" alt=\"nothing\" />")
					case "possible" => s.append("<img src=\"" + possible +"\" width=\"40\" height=\"40\" alt=\"possible\" />")
					case "redmark" => s.append("<img src=\"" + redmark +"\" width=\"40\" height=\"40\" alt=\"redmark\" />")
					case "greenmark" => s.append("<img src=\"" + greenmark +"\" width=\"40\" height=\"40\" alt=\"greenmark\" />")
				}

				s.append("</td>")
				
			}
			s.append("</tr>\n")
		}
		s.append("</table>\n")
		return s.toString
	}

}



















