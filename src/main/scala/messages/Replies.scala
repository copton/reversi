package messages

import java.util.HashMap

abstract class BaseReply extends Renderer

class RootReply extends BaseReply {

	def renderHtml(): String = {
		var result: String = "<div>\n"
		return result		
	}

	def renderJson(): String = { null }

	def renderXml(): String = {null}

}

class TournamentsReply extends BaseReply {

	def renderHtml(): String = {
		var result: String = "<div>\n"
		return result		
	}

	def renderJson(): String = { null }

	def renderXml(): String = {null}

}

class CurrentTurnReply(var board: player.GameBoard, var lastPosition: reversi.Position) extends BaseReply {


	def renderHtml(): String = {
		import reversi.{Position, Occupation, Color}

		val red: String = "/webresources/images/32x32/red.gif"
		val green: String = "/webresources/images/32x32/green.gif"
		val redmark: String = "/webresources/images/32x32/redmark.gif"
		val greenmark: String = "/webresources/images/32x32/greenmark.gif"
		val nothing: String = "/webresources/images/32x32/nothing.png"

		val s: StringBuffer = new StringBuffer
		s.append("<table>\n")
		
		for (i <- 0 to 7) {
			s.append("<tr>\n")
			for(j <- 0 to 7) {
				s.append("<td>")
				val pos: Position = new Position(i,j)
				val occ: Occupation = board.getOccupation(pos)
				occ match {
					case Color.RED => s.append("<img src=\"" + red +"\" width=\"40\" height=\"40\" alt=\"red\" />")
					case Color.GREEN => s.append("<img src=\"" + green +"\" width=\"40\" height=\"40\" alt=\"red\" />")
					case _ => s.append("<img src=\"" + nothing +"\" width=\"40\" height=\"40\" alt=\"nothing\" />")
				}

				s.append("</td>")
				
			}
			s.append("</tr>\n")
		}
		s.append("</table>\n")
		return s.toString
	}

	def renderJson(): String = { null }

	def renderXml(): String = {null}

}






















