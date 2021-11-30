package graphics

import game._
import map.{Edge, Node, World}
import org.scalajs.dom

import scala.scalajs.js
import org.scalajs.dom.html.{Canvas, Pre}

// Due to how html5 canvas coordinates work, Pos always refers to the top left most pixel of the element
case class Pos(var x: Int, var y: Int)

case class UIElement(pos: Pos, width: Int, height: Int, func: () => Unit)
case class StreetSelectBox(pos: Pos, width: Int, height: Int, street_value: Int)

// Window contains all code around ui drawing related functions

object Window {
  val c = dom.document.getElementById("canvas").asInstanceOf[Canvas]
  type Ctx2D = dom.CanvasRenderingContext2D
  val ctx = c.getContext("2d").asInstanceOf[Ctx2D]
  val w = 800
  val h = 600

  var canvasPos = new Pos(0,0)
  var mousePos = new Pos(0, 0)

  var elements = Vector[UIElement]()
  var lineElements = Map[Int, Pos]()

  // COLORS
  val darkBackground = "#19150C"
  val highlightYellow = "#ffbb22"
  val darkishRed = "#AE0903"
  val offwhite = "#fefad7"
  val greenishGrey = "#8c8757"

  // Checks if mouse is currently over a drawn UI element
  private def mouseIsOver(clicked: Boolean = false): Unit = {
    for (element <- elements) {
      if (this.mousePos.x > element.pos.x - element.width/2 && this.mousePos.x < element.pos.x + element.width/2) {
        if (this.mousePos.y > element.pos.y - element.height/2 && this.mousePos.y < element.pos.y + element.height/2) {
          println("hovering over an element")
          if (clicked)
            element.func()
        }
      }
    }
  }

  // Throwaway function
  private def alert() = {
    dom.window.alert("Clicked the box!")
  }

  // Assigns values from MouseEvent element to object attributes
  // aka updates mouse coordinates
  private def assignMouse(e: dom.MouseEvent): Unit = {
    var cRect = this.c.getBoundingClientRect()
    this.mousePos.x = e.pageX.toInt - cRect.left.toInt
    this.mousePos.y = e.pageY.toInt - cRect.top.toInt
    this.mouseIsOver()
  }

  // Executes the function listed in UIElement, of the element over which the mouse currently is on
  private def mouseClickCheck(e: dom.MouseEvent): Unit = {
    this.mouseIsOver(true)
  }

  private def StreetSelectBoxDimensionCalculator(boxes: Vector[StreetSelectBox]): Unit = {
    val boxPadding = 20
    val boxWidth = this.w / boxes.size - boxPadding
    val boxHeight = this.h / (boxes.size / 2) - boxPadding / 2

    var calculatedBoxes = Vector[StreetSelectBox]()
    for ((box, i) <- boxes.zipWithIndex) {
      calculatedBoxes = calculatedBoxes :+ new StreetSelectBox(new Pos(0,0), boxWidth, boxHeight, boxes(i).street_value)
    }
  }

  // Includes all the drawing related functions
  def draw(screen: Int, nodes: Vector[Node] = Vector[Node](), edges: Vector[Edge] = Vector[Edge]()) = {
    elements = Vector[UIElement]()
    lineElements = Map[Int, Pos]()

    def resetCanvas: Unit = {
      this.ctx.fillStyle = darkBackground
      this.ctx.fillRect(0, 0, Window.w, Window.h)
    }

    def drawElements: Unit = {
      this.ctx.fillStyle = darkishRed
      for (element <- elements) {
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

    def mainMenu: Unit = {
      val storyStartElement = new UIElement(new Pos(Window.w/2, Window.h/3), 100, 20, alert)
      elements = elements :+ storyStartElement

      drawElements
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

      for (node <- normalizedNodes) {
        elements = elements :+ new UIElement(node, 2, 2, alert)
      }

      println(nodes.size)
      println(edges.size)

      //drawElements
      drawLineElements(normalizationWithId, edges)
    }

    println("Printed in draw function")

    if (screen == 1) mainMenu
    else if (screen == 2) map

  }

  c.onmousemove = { (e: dom.MouseEvent ) => (assignMouse(e))}
  c.onclick = { (e: dom.MouseEvent) => (mouseClickCheck(e))}

  c.width = w
  c.height = h

  ctx.fillRect(0,0, w, h)

}
