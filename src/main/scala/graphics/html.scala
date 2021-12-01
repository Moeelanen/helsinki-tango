package graphics

//
//
// THIS IS THE CODE FOR DRAWING THE UI ON HTML CANVAS
//
//     does not include heavy commentation because
//     of it. Code is scalajs, and was not taught
//              during the course.
//
//

import game._
import world._
import org.scalajs.dom
import org.scalajs.dom.html.{Canvas, Pre}

// Due to how html5 canvas coordinates work, Pos always refers to the top left most pixel of the element
case class Pos(var x: Int, var y: Int)

// Datatypes used when specifying different elements. Could be improved into one solution, too much repetition
case class UIElement(pos: Pos, width: Int, height: Int, func: () => Unit, color: String = Window.offwhite, message: String = "")
case class StreetUIElement(pos: Pos, width: Int, height: Int, street: Edge, color: String = Window.highlightYellow)
case class StoryUIElement(pos: Pos, width: Int = Window.w - 50, height: Int = 175, message: String, color: String = Window.darkishRed)


// Window contains all code around ui drawing related functions
object Window {
  // ----- Main attributes -----
  val c = dom.document.getElementById("canvas").asInstanceOf[Canvas]
  type Ctx2D = dom.CanvasRenderingContext2D
  val ctx = c.getContext("2d").asInstanceOf[Ctx2D]
  val w = 800
  val h = 600

  var canvasPos = new Pos(0,0)
  var mousePos = new Pos(0, 0)

  // ---- Keeps track of elements on the UI -----
  var UIElements = Vector[UIElement]()
  var StreetUIElements = Vector[StreetUIElement]()
  var StoryUIElements = Vector[StoryUIElement]()
  var lineElements = Map[Int, Pos]()

  // COLORS
  val darkBackground = "#19150C"
  val highlightYellow = "#ffbb22"
  val darkishRed = "#AE0903"
  val offwhite = "#fefad7"
  val greenishGrey = "#8c8757"

  // Opens github in another tab
  private def openGitHub(): Unit = {
    dom.window.open("https://github.com/Moeelanen/helsinki-tango")
  }

  // Checks if mouse is currently over a drawn UI element. Runs the function tied to UI element on mouse click
  // Definitely not my proudest function TODO: make a helper function and use that for every vector instead of ctrl + C
  private def mouseIsOver(clicked: Boolean = false): Unit = {
    var temp_UIElements = Vector[UIElement]()
    for (element <- this.UIElements) {
      var modifiedElement = element
      if (this.mousePos.x > element.pos.x && this.mousePos.x < element.pos.x + element.width) {
        if (this.mousePos.y > element.pos.y && this.mousePos.y < element.pos.y + element.height) {
          modifiedElement = new UIElement(element.pos, element.width, element.height, element.func, this.darkishRed, element.message)
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
          if (clicked)
            Game.moveToNode(element.street.target)
        }
      }
      temp_StreetUIElements = temp_StreetUIElements :+ modifiedStreetElement
    }
    this.StreetUIElements = temp_StreetUIElements

    var temp_StoryUIElements = Vector[StoryUIElement]()
    for (element <- this.StoryUIElements) {
      var modifiedStoryElement = new StoryUIElement(element.pos, element.width, element.height, element.message, element.color)
      if (this.mousePos.x > element.pos.x && this.mousePos.x < element.pos.x + element.width) {
        if (this.mousePos.y > element.pos.y && this.mousePos.y < element.pos.y + element.height) {
          modifiedStoryElement = new StoryUIElement(element.pos, element.width, element.height, element.message, this.offwhite)
          if (clicked) {
            Game.moveToNode(7503)
            Game.currentScreen = 3
          }
        }
      }
      temp_StoryUIElements = temp_StoryUIElements :+ modifiedStoryElement
    }
    this.StoryUIElements = temp_StoryUIElements
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

  // Places the street selection boxes on screen
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
  def draw(screen: Int, nodes: Vector[Node] = Vector[Node](), edges: Vector[Edge] = Vector[Edge](), loading_dots: Int = 0, line: String = "")= {
    // This too, is extremely sloppy and quite frankly disgusting. Would benefit highly from better helper functions and more universally working
    // code, instead of making a new function for every single different UIElement type.
    //
    // Does not include lot of comments, due to likely refactoring

    // Resets canvas :)
    def resetCanvas: Unit = {
      this.UIElements = Vector[UIElement]()
      this.lineElements = Map[Int, Pos]()
      this.StreetUIElements = Vector[StreetUIElement]()
      this.StoryUIElements = Vector[StoryUIElement]()

      this.ctx.fillStyle = darkBackground
      this.ctx.fillRect(0, 0, Window.w, Window.h)
    }

    // Used now to just draw the main manu elements.
    def drawElements: Unit = {
      mouseIsOver()
      for (element <- this.UIElements) {
        this.ctx.fillStyle = element.color
        //this.ctx.fillRect(element.pos.x, element.pos.y, element.width, element.height)
        this.ctx.font = "20px 'Courier New'"
        this.ctx.fillText(element.message, element.pos.x + (element.width - ctx.measureText(element.message).width)/2, element.pos.y + element.height/2)
      }
    }

    // Draws lines to represent edges
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

    def drawSpeechBox(): Unit = {
      val element = this.StoryUIElements(0)
      this.ctx.fillStyle = element.color
      this.ctx.fillRect(element.pos.x, element.pos.y, element.width, element.height)
      this.ctx.fillStyle = this.darkBackground
      this.ctx.fillRect(element.pos.x + 14, element.pos.y + 14, element.width - 28, element.height - 28)

      this.ctx.font = "bold 20px 'Courier New'"
      this.ctx.fillStyle = this.offwhite

      val words = element.message.split(" ")
      var message: String = ""
      var lines = Vector[String]()
      for (word <- words) {
        var width = ctx.measureText(message + " " + word).width
        if (width < element.width - 30)
          message = message + word + " "
        else {
          lines = lines :+ message
          message = word + " "
        }
      }
      lines = lines :+ message

      for ((modified_line, i) <- lines.zipWithIndex) {
        this.ctx.fillText(modified_line, element.pos.x + 24, element.pos.y + (i+1) * 27 + 10)
      }

    }

    def mainMenu(): Unit = {
      resetCanvas
      val element_width: Int = 150
      val element_height: Int = 40
      val storyStartElement = new UIElement(new Pos(Window.w/2 - element_width/2, Window.h/3), element_width, element_height, Game.startFree, message = "Start")
      this.UIElements = this.UIElements :+ storyStartElement
      val openGitHubElement = new UIElement(new Pos(Window.w/2 - element_width/2, Window.h/2), element_width, element_height, openGitHub, message = "GitHub")
      this.UIElements = this.UIElements :+ openGitHubElement

      drawElements
    }

    def loading(): Unit = {
      resetCanvas
      this.ctx.font = "bold 40px 'Courier New'"
      this.ctx.fillStyle = this.offwhite
      this.ctx.fillText(s"Loading${"."*loading_dots}", this.w/2 - 130, this.h / 2 - 40)
    }

    def map(): Unit = {
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
    }

    def drive(): Unit = {
      map()

      def StreetBoxes(possibleStreets: Vector[Edge]): Unit = {
        var uiStreetBoxes = Vector[StreetUIElement]()
        for ((street, i) <- possibleStreets.zipWithIndex) {
          uiStreetBoxes = uiStreetBoxes :+ new StreetUIElement(new Pos(0,0), 0, 0, street)
        }
        StreetUIElementsFittingToCanvas(uiStreetBoxes)
        drawStreetUIElements
      }

      StreetBoxes(Game.possibleEdges)
    }

    def speech(): Unit = {
      //map()
      resetCanvas

      var element = new StoryUIElement(new Pos(25, this.h - 175 - 25), message = line)
      this.StoryUIElements = Vector(element)
      drawSpeechBox()
    }


    if (screen == 1) mainMenu()
    else if (screen == 2) loading()
    else if (screen == 3) drive()
    else if (screen == 4) speech()

  }

  // Event listeners
  c.onmousemove = { (e: dom.MouseEvent ) => (assignMouseCoordinates(e))}
  c.onclick = { (e: dom.MouseEvent) => (mouseClickCheck(e))}

  c.width = w
  c.height = h
}
