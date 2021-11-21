package map_creation

trait edge {
  val source_node: Int
  val target_node: Int
  val length: Double
  val bidirectional: Boolean
  val name: String
}

trait node {
  val id: Int
  val lat: Double
  val lon: Double
}
