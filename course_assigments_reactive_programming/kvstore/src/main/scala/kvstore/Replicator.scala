package kvstore

import akka.actor.Props
import akka.actor.Actor
import akka.actor.ActorRef
import scala.concurrent.duration._
import scala.language.postfixOps

object Replicator {
  case class Replicate(key: String, valueOption: Option[String], id: Long)
  case class Replicated(key: String, id: Long)
  
  case class Snapshot(key: String, valueOption: Option[String], seq: Long)
  case class SnapshotAck(key: String, seq: Long)

  def props(replica: ActorRef): Props = Props(new Replicator(replica))
}

class Replicator(val replica: ActorRef) extends Actor {
  import Replicator._
  import Replica._
  import context.dispatcher

  context.system.scheduler.schedule(100 millis, 100 millis, context.self, "retry")

  // map from sequence number to pair of sender and request
  var acks = Map.empty[Long, (ActorRef, Replicate)]
  // a sequence of not-yet-sent snapshots (you can disregard this if not implementing batching)
  var pending = Vector.empty[Snapshot]
  
  var _seqCounter = 0L
  def nextSeq = {
    val ret = _seqCounter
    _seqCounter += 1
    ret
  }
  
  /* TODO Behavior for the Replicator. */
  def receive: Receive = {
    case "retry" =>
      acks.foreach{entry=>
        val (seq, (primary, command)) = entry
        replica ! Snapshot(command.key, command.valueOption, seq)
      }
    case command:Replicate =>
      val seq = nextSeq
      acks += seq -> (sender, command)
      replica ! Snapshot(command.key, command.valueOption, seq)
    case ack:SnapshotAck =>
      acks.get(ack.seq).map{entry=>
        val (primary, command) = entry
        primary ! Replicated(command.key, command.id)
      }
      acks -= ack.seq
  }
}
