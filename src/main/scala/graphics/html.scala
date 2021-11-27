package graphics

import game._
import map.{Edge, Node}

import org.scalajs.dom

import scala.scalajs.js
import org.scalajs.dom.html.{Canvas, Pre}

case class Pos(var x: Int, var y: Int)
case class UIElement(pos: Pos, width: Int, height: Int, func: () => Unit)

object Window {
  val c = dom.document.getElementById("canvas").asInstanceOf[Canvas]
  type Ctx2D = dom.CanvasRenderingContext2D
  val ctx = c.getContext("2d").asInstanceOf[Ctx2D]
  val w = 800
  val h = 600

  var canvasPos = new Pos(0,0)
  var mousePos = new Pos(0, 0)

  var elements = Vector[UIElement]()

  // COLORS
  val highlightedUIElement = "#"

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

  private def alert() = {
    dom.window.alert("Clicked the box!")
  }

  private def assignMouse(e: dom.MouseEvent): Unit = {
    var cRect = this.c.getBoundingClientRect()
    this.mousePos.x = e.pageX.toInt - cRect.left.toInt
    this.mousePos.y = e.pageY.toInt - cRect.top.toInt
    this.mouseIsOver()
  }

  private def mouseClickCheck(e: dom.MouseEvent): Unit = {
    this.mouseIsOver(true)
  }

  def draw(screen: Int, nodes: Vector[Node] = Vector[Node]()) {
    elements = Vector[UIElement]()

    def resetCanvas: Unit = {
      this.ctx.fillStyle = "#000"
      this.ctx.fillRect(0, 0, Window.w, Window.h)
    }

    def drawElements: Unit = {
      resetCanvas
      this.ctx.fillStyle = "#FFF"
      for (element <- elements) {
        this.ctx.fillRect(element.pos.x - element.width/2, element.pos.y - element.height/2, element.width, element.height)
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


      def normalizer(node: Node): Pos = {
        // Lat increases to North, Lon increases to East
        // mouseY increases South, mouseX increases East
        val lat = (this.h - (node.lat - minLat) / latDifference * this.h)
        val lon = ((node.lon - minLon) / lonDifference * this.w)
        new Pos(lon.toInt, lat.toInt)
      }

      val normalizedNodes = nodes.map(normalizer)

      this.ctx.fillStyle = "#000"
      for (node <- normalizedNodes) {
        elements = elements :+ new UIElement(node, 2, 2, alert)
      }

      drawElements
    }



    if (screen == 1) mainMenu
    else if (screen == 2) map

  }

  c.onmousemove = { (e: dom.MouseEvent ) => (assignMouse(e))}
  c.onclick = { (e: dom.MouseEvent) => (mouseClickCheck(e))}

  c.width = w
  c.height = h

  ctx.fillRect(0,0, w, h)

}
