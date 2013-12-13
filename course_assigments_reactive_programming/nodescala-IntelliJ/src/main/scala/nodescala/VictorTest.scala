package nodescala

import scala.concurrent.{Promise, Await, Future}

import scala.async.Async._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object VictorTest {
  def main(args: Array[String]) {
    println(Await.result(Future.all(List(Future(10), Future(20))), 1 second))
    println(Await.result(Future.any(List(Future(delayedFunc(300)), Future(delayedFunc(200)), Future(delayedFailedFunc(500)))), 5 second))

    val working = Future.run() { ct =>
      async {
        while (ct.nonCancelled) {
          println("working")
          Thread.sleep(200)
        }
        println("done")
      }
    }

    Future.delay(1 seconds) onSuccess {
      case _ => working.unsubscribe()
    }

    val f1 = Future(5)
    val f2 = Future.delay(5 seconds)

    println(f1.now)
    println(f2.now)

    Thread.sleep(10000)
  }

  def delayedFunc(timing:Long) = {
    Thread.sleep(timing)
    timing
  }

  def delayedFailedFunc(timing:Long) = {
    Thread.sleep(timing)
    throw new Exception("Kuku")
  }
}
