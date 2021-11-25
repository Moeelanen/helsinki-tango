package map

import scala.util._
import scala.concurrent._
import ExecutionContext.Implicits.global
import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import scala.async.Async.{async, await}

// Defining data types for edges and nodes.
final case class Edge(source: Int, target: Int, oneway: Boolean, name: String, cardinal: String, distance: Double)
final case class Node(id: Int, x: Double, y: Double)


object World {

  // List of all private attributes
  private var nodes: Vector[Node] = Vector[Node]()
  private var edges: Vector[Edge] = Vector[Edge]()
  var map = Map[Int, Vector[Edge]]()

  // Populating the world with nodes and edges during initialisation
  // It took eight hours to figure out the following asynchronous code :) 

  private def initialize: Future[Unit] = async {
    println("Started initialization")
    
    def populatingEdges(value: String): Unit = {
      println("Started populating edges!")
      val edges = value.split("\n")

      for (line <- edges) {
        val cols = line.split(",").map(_.trim)
        val temp_edge = new Edge(cols(0).toInt, cols(1).toInt, cols(2).toBoolean, cols(3), cols(4), cols(5).toDouble)
        this.edges = this.edges :+ temp_edge
      }
      println("Edges completed!")
    }

    def populatingNodes(value: String): Unit = {
      println("Started populating nodes!")
      val nodes = value.split("\n")

      for (line <- nodes) {
        val cols = line.split(",").map(_.trim)
        val temp_node = new Node(cols(0).toInt, cols(1).toDouble, cols(2).toDouble)
        this.nodes = this.nodes :+ temp_node
      }
      println("Nodes completed!")
    }

    val buffering: Future[Unit] = async {
      val bufferedNodes = Ajax.get("http://moelanen.xyz/helsinki-tango/map_data/map.node")
        .map(xhr => populatingNodes(xhr.responseText))

      val bufferedEdges = Ajax.get("http://moelanen.xyz/helsinki-tango/map_data/map.edge")
        .map(xhr => populatingEdges(xhr.responseText))

      await(bufferedNodes)
      await(bufferedEdges)
    }

    await(buffering)
    this.map = this.edges.groupBy(_.source)
    println(this.map.head)
    println("Initialization completed!")

  }

  def run: Future[Unit] = async{
    await(this.initialize)
    println("Run initialization completed")

  }
}
