/**
 * Copyright (C) 2009-2013 Typesafe Inc. <http://www.typesafe.com>
 */
package actorbintree

import akka.actor._
import scala.collection.immutable.Queue
import actorbintree.BinaryTreeNode.CopyFinished

object BinaryTreeSet {

  trait Operation {
    def requester: ActorRef
    def id: Int
    def elem: Int
  }

  trait OperationReply {
    def id: Int
  }

  /** Request with identifier `id` to insert an element `elem` into the tree.
    * The actor at reference `requester` should be notified when this operation
    * is completed.
    */
  case class Insert(requester: ActorRef, id: Int, elem: Int) extends Operation

  /** Request with identifier `id` to check whether an element `elem` is present
    * in the tree. The actor at reference `requester` should be notified when
    * this operation is completed.
    */
  case class Contains(requester: ActorRef, id: Int, elem: Int) extends Operation

  /** Request with identifier `id` to remove the element `elem` from the tree.
    * The actor at reference `requester` should be notified when this operation
    * is completed.
    */
  case class Remove(requester: ActorRef, id: Int, elem: Int) extends Operation

  /** Request to perform garbage collection*/
  case object GC

  /** Holds the answer to the Contains request with identifier `id`.
    * `result` is true if and only if the element is present in the tree.
    */
  case class ContainsResult(id: Int, result: Boolean) extends OperationReply
  
  /** Message to signal successful completion of an insert or remove operation. */
  case class OperationFinished(id: Int) extends OperationReply

}


class BinaryTreeSet extends Actor {
  import BinaryTreeSet._
  import BinaryTreeNode._

  def createRoot: ActorRef = context.actorOf(BinaryTreeNode.props(0, initiallyRemoved = true))

  var root = createRoot

  var pendingQueue = Queue.empty[Operation]

  // optional
  def receive = normal

  def gc(){
    val newRoot = createRoot
    root ! CopyTo(newRoot)
    context.become(garbageCollecting(newRoot))
  }

  /** Accepts `Operation` and `GC` messages. */
  val normal: Receive = {
    case command:Insert =>root ! command
    case command:Contains =>root ! command
    case command:Remove =>root ! command
    case GC => gc()
  }

  def processQueue(newRoot: ActorRef, queue:Queue[Operation]) {
    if (!queue.isEmpty){
      val (command, rez) = queue.dequeue
      newRoot ! command
      processQueue(newRoot, rez)
    }
  }

  def finishGC(newRoot: ActorRef){
    processQueue(newRoot, pendingQueue)
    pendingQueue = Queue.empty[Operation]
    context.stop(root)
    root = newRoot
    context.become(normal)
  }

  /** Handles messages while garbage collection is performed.
    * `newRoot` is the root of the new binary tree where we want to copy
    * all non-removed elements into.
    */
  def garbageCollecting(newRoot: ActorRef): Receive = {
    case command:Operation =>pendingQueue = pendingQueue.enqueue(command)
    case GC =>
    case CopyFinished => finishGC(newRoot)
  }
}

object BinaryTreeNode {
  trait Position

  case object Left extends Position
  case object Right extends Position

  case class CopyTo(treeNode: ActorRef)
  case object CopyFinished

  def props(elem: Int, initiallyRemoved: Boolean) = Props(classOf[BinaryTreeNode],  elem, initiallyRemoved)
}

class BinaryTreeNode(val elem: Int, initiallyRemoved: Boolean) extends Actor {
  import BinaryTreeNode._
  import BinaryTreeSet._

  var subtrees = Map[Position, ActorRef]()
  var removed = initiallyRemoved

  def receive = normal

  def insert(command: Insert){
//    println(s"Insert ${command.elem} ${context.self}")
    val element = command.elem

    if (element < elem) {
      subtrees.get(Left) match {
        case Some(left) =>
          left ! command
        case None =>
          val left = context.actorOf(BinaryTreeNode.props(element, initiallyRemoved = false))
          subtrees += Left -> left
          command.requester ! OperationFinished(command.id)
      }
    } else if (element == elem) {
      if (removed)
        removed = false
      command.requester ! OperationFinished(command.id)
    } else if (element > elem) {
      subtrees.get(Right) match {
        case Some(right) =>
          right ! command
        case None =>
          val right = context.actorOf(BinaryTreeNode.props(element, initiallyRemoved = false))
          subtrees += Right -> right
          command.requester ! OperationFinished(command.id)
      }
    }
  }

  def contains(command: Contains){
    val element = command.elem
//    println(s"Contains ${command.elem} ${context.self}")

    if (element < elem) {
      subtrees.get(Left) match {
        case Some(left) =>
          left ! command
        case None =>
          command.requester ! ContainsResult(command.id, result = false)
      }
    } else if (element == elem) {
      command.requester ! ContainsResult(command.id, !removed)
    } else if (element > elem) {
      subtrees.get(Right) match {
        case Some(right) =>
          right ! command
        case None =>
          command.requester ! ContainsResult(command.id, result = false)
      }
    }
  }

  def remove(command: Remove){
    val element = command.elem
//    println(s"remove ${command.elem} ${context.self}")

    if (element < elem) {
      subtrees.get(Left) match {
        case Some(left) =>
          left ! command
        case None =>
          command.requester ! OperationFinished(command.id)
      }
    } else if (element == elem) {
      removed = true
      command.requester ! OperationFinished(command.id)
    } else if (element > elem) {
      subtrees.get(Right) match {
        case Some(right) =>
          right ! command
        case None =>
          command.requester ! OperationFinished(command.id)
      }
    }
  }

  def copyTo(command: CopyTo){
    if (!removed)
      command.treeNode ! Insert(self, 0, elem)

    subtrees.get(Left).map(_ ! command)
    subtrees.get(Right).map(_ ! command)

    processCopyingState(subtrees.values.toSet, removed)
  }

  /** Handles `Operation` messages and `CopyTo` requests. */
  val normal: Receive = {
    case command:Insert =>insert(command)
    case command:Contains =>contains(command)
    case command:Remove =>remove(command)
    case command:CopyTo =>copyTo(command)
  }

  // optional
  /** `expected` is the set of ActorRefs whose replies we are waiting for,
    * `insertConfirmed` tracks whether the copy of this node to the new tree has been confirmed.
    */
  def copying(expected: Set[ActorRef], insertConfirmed:Boolean): Receive = {
    case command:OperationFinished => processCopyingState(expected, true)
    case CopyFinished =>
      processCopyingState(expected - sender, insertConfirmed)
  }

  def processCopyingState(expected: Set[ActorRef], insertConfirmed:Boolean) {
    if (expected.isEmpty && insertConfirmed) {
      context.parent ! CopyFinished
      context.stop(self)
    } else {
      context.become(copying(expected, insertConfirmed))
    }
  }
}
