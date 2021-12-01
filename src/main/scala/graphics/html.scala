package graphics

import game._
import world._
import org.scalajs.dom

import scala.scalajs.js
import org.scalajs.dom.html.{Canvas, Pre}

import scala.async.Async.{async, await}


// Due to how html5 canvas coordinates work, Pos always refers to the top left most pixel of the element
case class Pos(var x: Int, var y: Int)

case class UIElement(pos: Pos, width: Int, height: Int, func: () => Unit, color: String = Window.darkishRed)
case class StreetUIElement(pos: Pos, width: Int, height: Int, street: Edge, color: String = Window.highlightYellow)

// Window contains all code around ui drawing related functions

object Window {
  val c = dom.document.getElementById("canvas").asInstanceOf[Canvas]
  type Ctx2D = dom.CanvasRenderingContext2D
  val ctx = c.getContext("2d").asInstanceOf[Ctx2D]
  val w = 800
  val h = 600

  var canvasPos = new Pos(0,0)
  var mousePos = new Pos(0, 0)

  var UIElements = Vector[UIElement]()
  var StreetUIElements = Vector[StreetUIElement]()
  var lineElements = Map[Int, Pos]()

  // COLORS
  val darkBackground = "#19150C"
  val highlightYellow = "#ffbb22"
  val darkishRed = "#AE0903"
  val offwhite = "#fefad7"
  val greenishGrey = "#8c8757"

  // Checks if mouse is currently over a drawn UI element
  private def mouseIsOver(clicked: Boolean = false): Unit = {
    var temp_UIElements = Vector[UIElement]()
    for (element <- this.UIElements) {
      var modifiedElement = element
      if (this.mousePos.x > element.pos.x - element.width/2 && this.mousePos.x < element.pos.x + element.width/2) {
        if (this.mousePos.y > element.pos.y - element.height/2 && this.mousePos.y < element.pos.y + element.height/2) {
          modifiedElement = new UIElement(element.pos, element.width, element.height, element.func, this.offwhite)
          if (clicked)
            element.func()
        }
      }
      temp_UIElements = temp_UIElements :+ modifiedElement
    }
    this.UIElements = temp_UIElements

    var temp_StreetUIElements = Vector[StreetUIElement]()
    for (element <- this.StreetUIElements) {
      var modifiedStreetElement = new StreetUIElement(element.pos, element.width, element.height, element.street, element.color)
      if (this.mousePos.x > element.pos.x && this.mousePos.x < element.pos.x + element.width) {
        if (this.mousePos.y > element.pos.y && this.mousePos.y < element.pos.y + element.height) {
          modifiedStreetElement = new StreetUIElement(element.pos, element.width, element.height, element.street, this.offwhite)
          println("Mouse is over streetUIelement")
          if (clicked)
            Game.moveToNode(element.street.target)
        }
      }
      temp_StreetUIElements = temp_StreetUIElements :+ modifiedStreetElement
    }
    this.StreetUIElements = temp_StreetUIElements
  }

  // Throwaway function
  private def alert() = {
    dom.window.alert("Clicked the box!")
  }

  // Assigns values from MouseEvent element to object attributes
  // aka updates mouse coordinates
  private def assignMouseCoordinates(e: dom.MouseEvent): Unit = {
    var cRect = this.c.getBoundingClientRect()
    this.mousePos.x = e.pageX.toInt - cRect.left.toInt
    this.mousePos.y = e.pageY.toInt - cRect.top.toInt
  }

  // Executes the function listed in UIElement, of the element over which the mouse currently is on
  private def mouseClickCheck(e: dom.MouseEvent): Unit = {
    this.mouseIsOver(true)
  }

  private def StreetUIElementsFittingToCanvas(boxes: Vector[StreetUIElement]): Unit = {
    val horizontalPadding = 50
    val verticalPadding = 30
    val boxWidth = this.w / 2 - horizontalPadding
    val boxHeight = (this.h / 5) - verticalPadding / 2

    var calculatedBoxes = Vector[StreetUIElement]()
    for ((box, i) <- boxes.zipWithIndex) {
      if (i == 0) {
        var x = horizontalPadding / 2
        var y = this.h - boxHeight - verticalPadding / 2
        calculatedBoxes = calculatedBoxes :+ new StreetUIElement(new Pos(x, y), boxWidth, boxHeight, boxes(i).street)
      } else if (i == 1) {
        var x = this.w / 2 + horizontalPadding / 2
        var y = this.h - boxHeight - verticalPadding / 2
        calculatedBoxes = calculatedBoxes :+ new StreetUIElement(new Pos(x, y), boxWidth, boxHeight, boxes(i).street)
      } else if (i == 2) {
        var x = horizontalPadding / 2
        var y = this.h - boxHeight * 2 - verticalPadding
        calculatedBoxes = calculatedBoxes :+ new StreetUIElement(new Pos(x, y), boxWidth, boxHeight, boxes(i).street)
      } else if (i == 3) {
        var x = this.w / 2 + horizontalPadding / 2
        var y = this.h - boxHeight * 2 - verticalPadding
        calculatedBoxes = calculatedBoxes :+ new StreetUIElement(new Pos(x, y), boxWidth, boxHeight, boxes(i).street)
      }
    }
    this.StreetUIElements = calculatedBoxes
  }

  // Includes all the drawing related functions
  def draw(screen: Int, nodes: Vector[Node] = Vector[Node](), edges: Vector[Edge] = Vector[Edge](), loading_dots: Int = 0) = {

    this.UIElements = Vector[UIElement]()
    lineElements = Map[Int, Pos]()

    def resetCanvas: Unit = {
      this.ctx.fillStyle = darkBackground
      this.ctx.fillRect(0, 0, Window.w, Window.h)
    }

    def drawElements: Unit = {
      mouseIsOver()
      for (element <- this.UIElements) {
        this.ctx.fillStyle = element.color
        this.ctx.fillRect(element.pos.x - element.width/2, element.pos.y - element.height/2, element.width, element.height)
      }
    }

    def drawLineElements(normalizedNodes: Map[Int, Pos], edges: Vector[Edge]): Unit = {
      this.ctx.strokeStyle = darkishRed
      for (edge <- edges) {
        var source_node = normalizedNodes.get(edge.source)
        var target_node = normalizedNodes.get(edge.target)

        if (source_node.isDefined && target_node.isDefined) {
          ctx.beginPath()
          ctx.moveTo(source_node.get.x, source_node.get.y)
          ctx.lineTo(target_node.get.x, target_node.get.y)
          ctx.stroke()
        }
      }
    }

    def drawStreetUIElements: Unit = {
      mouseIsOver()
      for (element <- this.StreetUIElements) {
        this.ctx.fillStyle = element.color
        this.ctx.fillRect(element.pos.x, element.pos.y, element.width, element.height)
        this.ctx.fillStyle = this.darkBackground
        this.ctx.fillRect(element.pos.x + 8, element.pos.y + 8, element.width - 16, element.height - 16)
        this.ctx.font = "bold 25px 'Courier New'"
        this.ctx.fillStyle = this.offwhite
        this.ctx.fillText(element.street.name, element.pos.x + 16, element.pos.y + element.height / 3)
        this.ctx.font = "20px 'Courier New'"
        this.ctx.fillText(s"(${element.street.cardinal})", element.pos.x + 50, element.pos.y + element.height / 6 * 5)
      }
    }

    def mainMenu: Unit = {
      resetCanvas
      val storyStartElement = new UIElement(new Pos(Window.w/2, Window.h/3), 100, 20, Game.startFree)
      this.UIElements = this.UIElements :+ storyStartElement

      drawElements
    }

    def loading: Unit = {
      resetCanvas
      this.ctx.font = "bold 40px 'Courier New'"
      this.ctx.fillStyle = this.offwhite
      this.ctx.fillText(s"Loading${"."*loading_dots}", this.w/2 - 130, this.h / 2 - 40)
    }

    def map: Unit = {
      resetCanvas

      val minLat = nodes.map(_.lat).min
      val maxLat = nodes.map(_.lat).max
      val latDifference = maxLat - minLat
      val minLon = nodes.map(_.lon).min
      val maxLon = nodes.map(_.lon).max
      val lonDifference = maxLon - minLon

      var normalizationWithId = Map[Int, Pos]()

      def normalizer(node: Node): Pos = {
        // Lat increases to North, Lon increases to East
        // mouseY increases South, mouseX increases East
        val lat = (this.h - (node.lat - minLat) / latDifference * this.h)
        val lon = ((node.lon - minLon) / lonDifference * this.w)
        val location = new Pos(lon.toInt, lat.toInt)

        normalizationWithId += node.id -> location
        location
      }

      val normalizedNodes = nodes.map(normalizer)

      drawLineElements(normalizationWithId, edges)
      StreetBoxes(Game.possibleEdges)
    }

    def StreetBoxes(possibleStreets: Vector[Edge]): Unit = {
      var uiStreetBoxes = Vector[StreetUIElement]()
      for ((street, i) <- possibleStreets.zipWithIndex) {
        uiStreetBoxes = uiStreetBoxes :+ new StreetUIElement(new Pos(0,0), 0, 0, street)
      }
      StreetUIElementsFittingToCanvas(uiStreetBoxes)
      drawStreetUIElements
    }


    if (screen == 1) mainMenu
    else if (screen == 2) loading
    else if (screen == 3) map

  }

  c.onmousemove = { (e: dom.MouseEvent ) => (assignMouseCoordinates(e))}
  c.onclick = { (e: dom.MouseEvent) => (mouseClickCheck(e))}

  c.width = w
  c.height = h

  ctx.fillRect(0,0, w, h)

}
