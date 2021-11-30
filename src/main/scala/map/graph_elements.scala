package map

import scala.util._
import scala.concurrent._
import ExecutionContext.Implicits.global
import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import scala.async.Async.{async, await}

// Defining data types for edges and nodes.
final case class Edge(source: Int, target: Int, oneway: Boolean, name: String, cardinal: String, distance: Double)
final case class Node(id: Int, lat: Double, lon: Double)

object World {


  // List of all private attributes
  private var nodes: Vector[Node] = Vector[Node]()
  private var edges: Vector[Edge] = Vector[Edge]()
  var map = Map[Int, Vector[Edge]]()

  // Populating the world with nodes and edges during initialisation
  // It took eight hours to figure out the following asynchronous code :) 

  def initialize: Future[Unit] = async {
    println("Started initialization")
    
    def populatingEdges(value: String): Unit = {
      println("Started populating edges!")
      val edges = value.split("\n")

      for (line <- edges) {
        try {
          val cols = line.split(",").map(_.trim)
          val temp_edge = new Edge(cols(0).toInt, cols(1).toInt, cols(2).toBoolean, cols(3), cols(4), cols(5).toDouble)
          this.edges = this.edges :+ temp_edge
        } catch {
          case e: Throwable => println(e)
        }
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
    println(this.map.maxBy(n => n._2.size))
    println("Initialization completed!")
  }

  def getNode(id: Int): Node = {
    this.nodes(id)
  }

  def fetchSurroundingNodes(node: Node): Vector[Node] = {
    this.nodes.filter(n => ((n.lon - node.lon).abs < 0.01) && ((n.lat- node.lat).abs < 0.005))
  }

  def fetchSurrondingEdges(nodes: Vector[Node]): Vector[Edge] = {
    var surroundingEdges = Vector[Edge]()
    for (node <- nodes) {
      var edges = this.map.get(node.id)
      if (edges.isDefined)
        surroundingEdges = surroundingEdges ++ edges.get
    }
    surroundingEdges
  }

}
