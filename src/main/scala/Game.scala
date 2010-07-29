import se.scalablesolutions.akka.actor.Actor
import se.scalablesolutions.akka.actor.ActorRef
import se.scalablesolutions.akka.actor.Actor._
import se.scalablesolutions.akka.remote.{RemoteClient, RemoteNode}
import se.scalablesolutions.akka.util.Logging
import java.lang.{ProcessBuilder,Process}
import java.lang.Thread
import java.io._

class Dumper(in: InputStream) extends Thread {
	override def run() {
		val reader = new BufferedReader(new InputStreamReader(in))
		var line: String = ""
		do {
			line = reader.readLine()
			println("#####: " + line)
		} while(line != null)
	}
}

case class RunPlayer()

class Game extends Actor with Logging {
	var player: ActorRef = _

	def runPlayer {
		val cp = "/home/alex/scm/reversi/lib_managed/scala_2.8.0.RC3/compile/*:/home/alex/scm/reversi/target/scala_2.8.0.RC3/classes:/home/alex/scm/reversi/project/boot/scala-2.8.0.RC3/lib/scala-library.jar"
		println("MARK 2")

		val p = new ProcessBuilder("/opt/scala/bin/scala", "-cp", cp, "Server", "9998").start()
		(new Dumper(p.getErrorStream())).start()
		(new Dumper(p.getInputStream())).start()
	}

	def receive = {
		case RunPlayer => runPlayer

		case "Started" =>
			player = self.sender.get						
			val result = player !! "Hello"
			log.info("Result from Remote Actor: '%s'", result.get)
	}
}

object RunGame {
	def main(args: Array[String]) {
		val game = actorOf[Game]

		RemoteNode.start("localhost", 9998)
		RemoteNode.register("game", game)

		game.start
		game ! RunPlayer
	}
}
