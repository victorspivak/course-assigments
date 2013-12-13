package nodescala

import scala.language.postfixOps
import scala.util.{Try, Success, Failure}
import scala.collection._
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.async.Async.{async, await}
import org.scalatest._
import NodeScala._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

object My {
  val lSuccess = List(Future.always(10), Future.always(20), Future.always(100))
                                                  //> lSuccess  : List[scala.concurrent.Future[Int]] = List(scala.concurrent.impl.
                                                  //| Promise$DefaultPromise@13fb15, scala.concurrent.impl.Promise$DefaultPromise@
                                                  //| 1c94ff3, scala.concurrent.impl.Promise$DefaultPromise@1a014e1)
  val lFailed = List(Future.always(10), Future.always(20), Future.failed(new Exception("Hahaha")))
                                                  //> lFailed  : List[scala.concurrent.Future[Int]] = List(scala.concurrent.impl.P
                                                  //| romise$DefaultPromise@1880571, scala.concurrent.impl.Promise$DefaultPromise@
                                                  //| 11d5b39, scala.concurrent.impl.Promise$KeptPromise@5003f6)

  val f = Future.all(lSuccess)                    //> f  : scala.concurrent.Future[List[Int]] = scala.concurrent.impl.Promise$Defa
                                                  //| ultPromise@1c77610
  val r = Await.result(f, 10 seconds)             //> r  : List[Int] = List(10, 20, 100)
  
}