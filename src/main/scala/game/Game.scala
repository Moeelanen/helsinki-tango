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
  val volume = 0.7

  //------------------------------------------------------------

  //------------------- GAME STATE VALUES ----------------------

  val targetNode: Int = 39630
  var currentNode: Node = new Node(0, 0, 0)
  var traveledEdges: Vector[Edge] = Vector[Edge]()

  // CurrentScreen value clarifications
  // 1: Main Screen
  // 2: Loading Screen
  // 3: Driving
  // 4: Speech
  var currentScreen: Int = 1
  var dotCount = 1

  var possibleEdges: Vector[Edge] = Vector[Edge]()

  //------------------------------------------------------------

  // Starts playing audio
  private def playAudio(): Unit = {
    audio.volume = this.volume
    audio.play()
  }

  // Function to launch game from main screen
  def startFree(): Unit = async{
    playAudio()
    this.currentScreen = 2
    await(World.initialize)
    js.timers.setTimeout(1000) {
      this.currentScreen = 4
    }
  }

  // The "main loop" of the game. Moves the character around
  def moveToNode(id: Int): Unit = {
    currentNode = World.getNode(id)
    possibleEdges = World.fetchPossibleEdgesReadable(currentNode)
    // Automatically move if there is only one road to go down
    if (possibleEdges.size == 1) {
      // A slight pause between frames
      js.timers.setTimeout(500) {
        moveToNode(possibleEdges.head.target)
      }
    }
    // TODO: SCORING SYSTEM NOT YET WORKING
    // Navigator.calculatePath(currentNode.id, targetNode)
  }

  // Game run ticker. "Main" logic is ran here
  def run(): Unit = {
    async {
      // Continuous ticker. Runs the code every frame time
      js.timers.setInterval(frameTime) {
        // Choosing what to draw based on current status
        // Look at the clarification above to understand what the values mean
        if (currentScreen == 1) {
          Window.draw(currentScreen)
        } else if(currentScreen == 2) {
          // TODO: Add loading animation
          Window.draw(currentScreen, loading_dots=3)
        } else if(currentScreen == 3) {
          var surroundingNodes = World.fetchSurroundingNodes(this.currentNode)
          var surroundingEdges = World.fetchSurrondingEdges(surroundingNodes)
          Window.draw(currentScreen, surroundingNodes, surroundingEdges)
        } else if(currentScreen == 4) {
          var surroundingNodes = World.fetchSurroundingNodes(this.currentNode)
          var surroundingEdges = World.fetchSurrondingEdges(surroundingNodes)
          // TODO: Implement randomized start and end areas
          Window.draw(currentScreen, surroundingNodes, surroundingEdges, line = Story.customer_intro_line_1("Mannerheimintie", "Southwest", "Simonkatu", "Southeast"))
        }
      }
    }
  }

}
