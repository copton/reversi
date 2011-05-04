package game

import java.lang.{ProcessBuilder,Process}
import akka.util.Logging
import java.io.{InputStream, BufferedReader, InputStreamReader}
import akka.actor.ActorRef
import scala.util.Properties
import scala.io.Source
import messages._

class StreamLogger(prefix: String, in: InputStream, logf: ((=>String) => Unit)) extends Thread {
  override def run() = Source.fromInputStream(in).getLines.foreach(line => logf("%s: %s".format(prefix, line)))
}

class PlayerProc(val player: Player, val game: ActorRef, gamePort: Int) extends Thread with Logging {
  val proc = new ProcessBuilder(Properties.scalaCmd,
                                "-cp",
                                PlayerProc.cp,
                                "player.RunPlayer",
                                player.port.toString,
                                gamePort.toString,
				player.namingNumber.toString).start()
  val input = new StreamLogger("player" + player.port, proc.getInputStream(), log.info)
  val output = new StreamLogger("player" + player.port, proc.getErrorStream(), log.error)

  start()

  override def run() {
    input.start()
    output.start()
    input.join()
    output.join()
    val exitCode = proc.waitFor() 
    game ! _root_.messages.PlayerExit(player, exitCode)
    println("PlayerProc: end of run() reached")
  }
	
  def destroyProc() {
	proc.destroy()
	println("PlayerProc: proc destroyed!")
  }

}

object PlayerProc {
  val SCALA_VERSION = Properties.scalaPropOrEmpty("version.number").replace(".final", "")
  val cp = List("lib_managed/scala_%s/compile/*",
                "target/scala_%s/classes",
                "project/boot/scala-%s/lib/scala-library.jar",
                "target/scala_%s/resources") map (_.format(SCALA_VERSION)) mkString ":"
}
