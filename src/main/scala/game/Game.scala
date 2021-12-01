package game

import graphics._
import world._
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLAudioElement

import scala.async.Async.{async, await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js


object Game {
  //------------------------- SETTINGS -------------------------

  // Graphical settings
  val frameTime = 1000/20

  // Audio settings
  val audio = dom.document.getElementById("soundtrack").asInstanceOf[HTMLAudioElement]
  val volume = 0.8

  //------------------------------------------------------------

  //------------------- GAME STATE VALUES ----------------------
  
  var currentNode: Node = new Node(0, 0, 0)
  var traveledEdges: Vector[Edge] = Vector[Edge]()

  // CurrentScreen value clarifications
  // 1: Main Screen
  // 2: Loading Screen
  // 3: Freeroam
  var currentScreen: Int = 1
  var dotCount = 1

  var possibleEdges: Vector[Edge] = Vector[Edge]()

  //------------------------------------------------------------

  private def playAudio(): Unit = {
    audio.volume = this.volume
    audio.play()
  }

  def startFree(): Unit = async{
    playAudio()
    this.currentScreen = 2
    await(World.initialize)
    js.timers.setTimeout(1000) {
      this.moveToNode(4428)
      this.currentScreen = 3
    }
  }

  def moveToNode(id: Int): Unit = {
    currentNode = World.getNode(id)
    possibleEdges = World.fetchPossibleEdgesAtNode(currentNode)
    if (possibleEdges.size == 1) {
      js.timers.setTimeout(500) {
        moveToNode(possibleEdges.head.target)
      }
    }
  }

  def run(): Unit = {
    async {
      js.timers.setInterval(frameTime) {
        if (currentScreen == 1) {
          Window.draw(currentScreen)
        } else if(currentScreen == 2) {
          // TODO: Add loading animation
          Window.draw(currentScreen, loading_dots=3)
        } else if(currentScreen == 3) {
          var surroundingNodes = World.fetchSurroundingNodes(this.currentNode)
          var surroundingEdges = World.fetchSurrondingEdges(surroundingNodes)
          Window.draw(currentScreen, surroundingNodes, surroundingEdges)
        }
      }
    }
  }

}
