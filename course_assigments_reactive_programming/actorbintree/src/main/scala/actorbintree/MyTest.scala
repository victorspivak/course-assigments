package actorbintree

import akka.actor.{ Props, ActorRef, ActorSystem }
import akka.testkit.{ TestProbe, ImplicitSender, TestKit }
import scala.concurrent.duration._

object MyTest{
  def main(args: Array[String]) {
    val obj = new MyTest

//    obj.test1()
    obj.test2()
  }
}

class MyTest(_system: ActorSystem) extends TestKit(_system) with ImplicitSender{
  def this() = this(ActorSystem("BinTree"))
  def shutdown() = system.shutdown()

  import actorbintree.BinaryTreeSet._

  def test1() {
    val topNode = system.actorOf(Props[BinaryTreeSet])

    topNode ! Contains(testActor, id = 1, 1)

    val requester = TestProbe()
    val requesterRef = requester.ref
    val ops = List(
      Insert(requesterRef, id=100, 1),
      Contains(requesterRef, id=50, 2),
      Remove(requesterRef, id=10, 1),
      Insert(requesterRef, id=20, 2),
      Contains(requesterRef, id=80, 1),
      Contains(requesterRef, id=70, 2)
    )

    val expectedReplies = List(
      OperationFinished(id=10),
      OperationFinished(id=20),
      ContainsResult(id=50, false),
      ContainsResult(id=70, true),
      ContainsResult(id=80, false),
      OperationFinished(id=100)
    )

    verify(requester, ops, expectedReplies)
    shutdown()
  }

  def test2() {
    val requester = TestProbe()
    val requesterRef = requester.ref
    val ops = List(

      Insert(requesterRef, id=10, 1),
      Insert(requesterRef, id=20, 2),
      Insert(requesterRef, id=30, 3),
      Remove(requesterRef, id=40, 3),
      Remove(requesterRef, id=50, 1),
      Contains(requesterRef, id=60, 1),
      Contains(requesterRef, id=70, 2),
      Contains(requesterRef, id=80, 3)
    )

    val tree = receiveBatch(requester, ops)

    tree ! GC

    shutdown()
  }

  def verify(probe: TestProbe, ops: Seq[Operation], expected: Seq[OperationReply]): Unit = {
    val topNode = system.actorOf(Props[BinaryTreeSet])

    ops foreach { op =>
      topNode ! op
    }

    receiveN(probe, ops, expected)
  }

  def receiveBatch(requester: TestProbe, ops: Seq[Operation]) = {
    val topNode = system.actorOf(Props[BinaryTreeSet])

    ops foreach { op =>
      topNode ! op
    }

    within(10.seconds) {
      val repliesUnsorted = for (i <- 1 to ops.size) yield try {
        requester.expectMsgType[OperationReply]
      } catch {
        case ex: Throwable if ops.size > 10 => throw new Exception(s"failure to receive confirmation $i/${ops.size}", ex)
        case ex: Throwable                  => throw new Exception(s"failure to receive confirmation $i/${ops.size}\nRequests:" + ops.mkString("\n    ", "\n     ", ""), ex)
      }

      repliesUnsorted.map(println)
      topNode
    }
  }

  def receiveN(requester: TestProbe, ops: Seq[Operation], expectedReplies: Seq[OperationReply]): Unit =
    within(10.seconds) {
      val repliesUnsorted = for (i <- 1 to ops.size) yield try {
        requester.expectMsgType[OperationReply]
      } catch {
        case ex: Throwable if ops.size > 10 => throw new Exception(s"failure to receive confirmation $i/${ops.size}", ex)
        case ex: Throwable                  => throw new Exception(s"failure to receive confirmation $i/${ops.size}\nRequests:" + ops.mkString("\n    ", "\n     ", ""), ex)
      }

      repliesUnsorted.map(println)

      val replies = repliesUnsorted.sortBy(_.id)
      if (replies != expectedReplies) {
        val pairs = (replies zip expectedReplies).zipWithIndex filter (x => x._1._1 != x._1._2)
        throw new Exception("unexpected replies:" + pairs.map(x => s"at index ${x._2}: got ${x._1._1}, expected ${x._1._2}").mkString("\n    ", "\n    ", ""))
      }
    }
}
