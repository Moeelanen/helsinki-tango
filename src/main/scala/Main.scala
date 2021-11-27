import map._
import graphics._

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.async.Async.{async, await}

object Main {
  def main(args: Array[String]): Unit = async {
    Window
    //await(World.run)
    //println("World ran")
  }
}
