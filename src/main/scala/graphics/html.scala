package graphics

import org.scalajs.dom

import scala.scalajs.js
import org.scalajs.dom.html.{Canvas, Pre}

case class Pos(var x: Int, var y: Int)

object Window {
  val c = dom.document.getElementById("canvas").asInstanceOf[Canvas]
  type Ctx2D = dom.CanvasRenderingContext2D
  val ctx = c.getContext("2d").asInstanceOf[Ctx2D]
  val w = 800
  val h = 600

  var canvasPos = new Pos(0,0)
  var mousePos = new Pos(0, 0)

  private def mouseIsOver(): Unit = {}

  private def assignMouse(e: dom.MouseEvent): Unit = {
    var cRect = this.c.getBoundingClientRect()
    this.mousePos.x = e.pageX.toInt - cRect.left.toInt
    this.mousePos.y = e.pageY.toInt - cRect.top.toInt
    println(this.mousePos.y)
    this.draw
  }

  private def mouseClickCheck(e: dom.MouseEvent): Unit = {
    if (this.mousePos.x > this.w/2-25 && this.mousePos.x < this.w/2+25) {
      if (this.mousePos.y > this.h/2-10 && this.mousePos.y < this.h/2+10) {
        dom.window.alert("Clicked the box!")
      }
    }
  }

  private def draw: Unit = {
    this.ctx.fillStyle = "#000"
    this.ctx.fillRect(0,0,this.w, this.h)
    this.ctx.fillStyle = "#FFF"
    this.ctx.fillRect(this.w / 2 - 25, this.h / 2 - 10, 50, 20)

    this.ctx.fillRect(this.mousePos.x - 5, this.mousePos.y - 5, 10, 10)
  }

  c.onmousemove = { (e: dom.MouseEvent ) => (assignMouse(e))}
  c.onclick = { (e: dom.MouseEvent) => (mouseClickCheck(e))}

  c.width = w
  c.height = h

  ctx.fillRect(0,0, w, h)

}
