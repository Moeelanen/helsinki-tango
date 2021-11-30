package game

import graphics._
import map.{World, Node, Edge}
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
    async {
      await(World.initialize)
      currentNode = World.getNode(32152)
      println("Fetching surrounding nodes")
      var surroundingNodes = World.fetchSurroundingNodes(this.currentNode)
      println("Done")
      println("Fetching surrounding edges")
      var surroundingEdges = World.fetchSurrondingEdges(surroundingNodes)
      println("Done")
      Window.draw(currentScreen, surroundingNodes, surroundingEdges)
      js.timers.setInterval(frameTime) {
      }
    }
  }

}
