package map

import scala.io.Source

// Defining data types for edges and nodes.
final case class Edge(source_node: Int, target_node: Int, length: Double, bidirectional: Boolean, name: String)
final case class Node(id: Int, x: Double, y: Double)

// Defining the adjacent graph
final case class EdgeWeightedGraph(adj: Map[Int, Vector[Edge]] = Map.empty)

object World {

  // List of all private attributes
  private var nodes: Vector[Node] = Vector[Node]()
  private var edges: Vector[Edge] = Vector[Edge]()
  var map: EdgeWeightedGraph = EdgeWeightedGraph()

  // Populating the world with nodes and edges during initialisation
  // Probably there exists a better way of doing this, but due to time I'll leave it like this for now
  // TODO: Wrap the code in a function and execute that to make things cleaner

  // Opening the map data files
  private val bufferedNodes = Source.fromFile("src/main/scala/map/map_data/processed/map.node")
  private val bufferedEdges = Source.fromFile("src/main/scala/map/map_data/processed/map.edge")


  // Iterating through the files (again, probably not the best way!)
  for (line <- bufferedEdges.getLines()) {
    val cols = line.split(",").map(_.trim)
    val temp_edge = new Edge(cols(0).toInt, cols(1).toInt, cols(2).toDouble, cols(3).toBoolean, cols(4))
    this.edges = this.edges :+ temp_edge
  }

  // Same process but for the nodes
  for (line <- bufferedNodes.getLines()) {
    val cols = line.split(",").map(_.trim)
    val temp_node = new Node(cols(0).toInt, cols(1).toDouble, cols(2).toDouble)
    this.nodes = this.nodes :+ temp_node
  }

  // Closing the open files to save resources
  this.bufferedNodes.close()
  this.bufferedEdges.close()

  // Populating the adjacency list
  this.map = EdgeWeightedGraph(this.edges.groupBy(_.source_node))
  println(this.map.adj.size)

  def firstEdge: Edge = {
    edges.head
  }

  def firstNode: Node = {
    nodes.head
  }

}