package gameserver.ports

class PortService {

	val tournamentPort: Int = 9999
	val gamePort: Int = 10000
	var basePlayerPort = 10001

	def getPlayerPort: Int = {
		val temp = basePlayerPort
		basePlayerPort = basePlayerPort + 1
		return temp	
	}

}
