package game

import java.lang.{ProcessBuilder,Process}
import se.scalablesolutions.akka.util.Logging
import java.io._

class StreamLogger(in: InputStream) extends Thread with Logging {
	override def run() {
		val reader = new BufferedReader(new InputStreamReader(in))
		var line: String = ""
		do {
			line = reader.readLine()
			if (line != null) {
				log.info(line)
			}
		} while(line != null)
	}
}

class OutputLogger(proc: Process) {
	val input = new StreamLogger(proc.getInputStream())
	val output = new StreamLogger(proc.getErrorStream())
	input.start()
	output.start()
}

class PlayerProc(val player: Player, gamePort: Int) {
	val proc = new ProcessBuilder("/opt/scala/bin/scala", "-cp", PlayerProc.cp, "player.RunPlayer", player.name, player.port.toString, gamePort.toString).start()
	val logger = new OutputLogger(proc)
}

object PlayerProc {
	val cp = "lib_managed/scala_2.8.0.RC3/compile/*:target/scala_2.8.0.RC3/classes:project/boot/scala-2.8.0.RC3/lib/scala-library.jar:target/scala_2.8.0.RC3/resources"
}
