package game

import java.lang.{ProcessBuilder,Process}
import se.scalablesolutions.akka.util.Logging
import java.io.{InputStream, BufferedReader, InputStreamReader}
import se.scalablesolutions.akka.actor.ActorRef

class StreamLogger(prefix: String, in: InputStream, logf: ((=>String) => Unit)) extends Thread {
	override def run() {
		val reader = new BufferedReader(new InputStreamReader(in))
		var line: String = ""
		do {
			line = reader.readLine()
			if (line != null) {
				logf(prefix + ": " + line)
			}
		} while(line != null)
	}
}

class PlayerProc(val player: Player, val game: ActorRef, gamePort: Int) extends Thread with Logging {
	val proc = new ProcessBuilder("/opt/scala/bin/scala", "-cp", PlayerProc.cp, "player.RunPlayer", player.port.toString, gamePort.toString).start()
    val input = new StreamLogger("player" + player.port, proc.getInputStream(), log.info)
	val output = new StreamLogger("player" + player.port, proc.getErrorStream(), log.error)

  start()

  override def run() {
    input.start()
    output.start()
    input.join()
    output.join()
    val exitCode = proc.waitFor() 
    game ! PlayerExit(player, exitCode)
  }
}

object PlayerProc {
	val cp = "lib_managed/scala_2.8.0.RC3/compile/*:target/scala_2.8.0.RC3/classes:project/boot/scala-2.8.0.RC3/lib/scala-library.jar:target/scala_2.8.0.RC3/resources"
}
