package game

import graphics._
import map._
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLAudioElement

import scala.async.Async.{async, await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js


object Game {
  //------------------------- SETTINGS -------------------------

  // Graphical settings
  val frameTime = 1000/60

  // Audio settings
  val audio = dom.document.getElementById("soundtrack").asInstanceOf[HTMLAudioElement]
  val volume = 0.8

  //------------------------------------------------------------

  //------------------- GAME STATE VALUES ----------------------
  
  var currentNode: Node = new Node(0, 0, 0)
  var traveledEdges: Vector[Edge] = Vector[Edge]()

  val currentScreen: Int = 2
  //
  //------------------------------------------------------------

  def playAudio(): Unit = {
    audio.volume = this.volume
    audio.play()
  }

  def run(): Unit = {
    println("Troubleshooting")
    async {
      await(World.initialize)
      currentNode = World.getNode(38670)
      Window.draw(currentScreen, World.fetchSurroundings(this.currentNode))
      js.timers.setInterval(frameTime) {
      }
    }
  }

}
