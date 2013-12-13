package suggestions

import rx.lang.scala.Observable
import scala.concurrent.duration._

object MyTest {
  def main(args: Array[String]) {
    val clock = Observable.interval(100 millis)

    takeIt(clock)
    takeIt(clock)
  }

  def takeIt(clock: Observable[Long]) {
    Thread.sleep(1000)

    val data = clock.take(10)

    println(data.toBlockingObservable.toList)
  }
}
