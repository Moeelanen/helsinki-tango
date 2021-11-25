package graphics

import org.scalajs.dom

import scala.scalajs.js
import org.scalajs.dom.html.Canvas

case class Pos(x: Int, y: Int)

object Window {
  val c = dom.document.getElementById("canvas").asInstanceOf[Canvas]
  type Ctx2D = dom.CanvasRenderingContext2D
  val ctx = c.getContext("2d").asInstanceOf[Ctx2D]
  val w = 800
  val h = 600
  c.width = w
  c.height = h

  ctx.fillRect(0,0, w, w)

  ctx.strokeStyle = "red"
  ctx.lineWidth = 3
  ctx.beginPath()
  ctx.moveTo(w/3, 0)
  ctx.lineTo(w/3, w/3)
  ctx.moveTo(w*2/3, 0)
  ctx.lineTo(w*2/3, w/3)
  ctx.moveTo(w, w/2)
  ctx.arc(w/2, w/2, w/2, 0, 3.14)

  ctx.stroke()
}
