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

object My {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(497); 
  val lSuccess = List(Future.always(10), Future.always(20), Future.always(100));System.out.println("""lSuccess  : List[scala.concurrent.Future[Int]] = """ + $show(lSuccess ));$skip(99); 
  val lFailed = List(Future.always(10), Future.always(20), Future.failed(new Exception("Hahaha")));System.out.println("""lFailed  : List[scala.concurrent.Future[Int]] = """ + $show(lFailed ));$skip(32); 

  val f = Future.all(lSuccess);System.out.println("""f  : scala.concurrent.Future[List[Int]] = """ + $show(f ));$skip(38); 
  val r = Await.result(f, 10 seconds);System.out.println("""r  : List[Int] = """ + $show(r ))}
  
}
