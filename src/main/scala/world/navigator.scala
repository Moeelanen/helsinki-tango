package world

import scala.scalajs.js.Math.{pow, sqrt}

case class AStarNode(ID: Int, parentID: Int, distance: Double, heuristic: Double, cost: Double)

object Navigator {

  // TODO: Finish creating the algorithm and implement it in the game

  def calculatePath(starting_node: Int, target_node: Int): Unit = {
    var open_list = Vector[AStarNode]()
    var closed_list = Vector[AStarNode]()


    var count: Int = 0
    var current_node = World.getNode(starting_node)


    def calculateHeuristic(from: Node, to: Node): Double = {
      sqrt(pow(to.lon - from.lon, 2) + pow(to.lat - from.lat, 2))
    }

    def updateOpenList(): Unit = {
      // Fetches all of the nearest connected Nodes
      val nearestNodes = World.fetchPossibleEdgesAtNode(current_node)
      // Temporary holder for AStarNodes
      var additionalOpenNodes = Vector[AStarNode]()

      // Forming the AStarNodes and adding them to the holder
      for (node <- nearestNodes) {

        // The ID is the target node ID
        val ID = node.target
        // Parent ID is the ID we are currently at
        val parentID = node.source
        // Distance is gotten straight from the data
        val distance = node.distance
        // Calculating the heuristic value, which in this case is just pythagorean theorem
        val heuristic = calculateHeuristic(World.getNode(ID), World.getNode(target_node))
        // Cost is the metric used for choosing which node to go down
        val cost = distance + heuristic

        additionalOpenNodes = additionalOpenNodes :+ new AStarNode(ID, parentID, distance, heuristic, cost)
      }
      open_list = open_list ++ additionalOpenNodes
    }

    def chooseNode(): Unit = {
      // Get the node with the lowest cost
      var current_AStarNode = open_list.minBy(n => n.cost)
      // Set that as the current node
      current_node = World.getNode(current_AStarNode.ID)
      // Remove that from the open list
      open_list = closed_list.filterNot(_.ID == current_AStarNode.ID)
      // And add it to the closed list
      closed_list = closed_list :+ current_AStarNode
    }

    def calculationComplete(): Boolean = {
      current_node.id == target_node || count > 100000
    }

    def backtrack(parentid: Int): Vector[Node] = {
      var route = Vector[Node]()

      var parent_node = closed_list.find(_.ID == parentid)
      route = route :+ World.getNode(parent_node.get.ID)
      if (parent_node.get.parentID != starting_node)
        backtrack(parent_node.get.parentID)

      route
    }

    while (!calculationComplete()) {
      print(count)
      updateOpenList()
      chooseNode()
      count += 1
    }

    println(closed_list.size)

  }
}
