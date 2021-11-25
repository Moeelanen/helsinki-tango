import map._

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.async.Async.{async, await}

object Main {
  def main(args: Array[String]): Unit = async {
    println("Hello, World!")
    await(World.run)
    println("World ran")
  }
}
