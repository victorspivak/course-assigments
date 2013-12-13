package kvstore

import akka.actor._
import kvstore.Arbiter._
import scala.collection.immutable.Queue
import akka.actor.SupervisorStrategy.{Resume, Stop, Escalate, Restart}
import scala.annotation.tailrec
import akka.pattern.{ ask, pipe }
import scala.concurrent.duration._
import akka.util.Timeout
import java.io.IOException
import scala.Some
import akka.actor.OneForOneStrategy
import scala.language.postfixOps

object Replica {
  sealed trait Operation {
    def key: String
    def id: Long
  }
  case class Insert(key: String, value: String, id: Long) extends Operation
  case class Remove(key: String, id: Long) extends Operation
  case class Get(key: String, id: Long) extends Operation

  sealed trait OperationReply
  case class OperationAck(id: Long) extends OperationReply
  case class OperationFailed(id: Long) extends OperationReply
  case class GetResult(key: String, valueOption: Option[String], id: Long) extends OperationReply

  def props(arbiter: ActorRef, persistenceProps: Props): Props = Props(new Replica(arbiter, persistenceProps))
}

class Replica(val arbiter: ActorRef, persistenceProps: Props) extends Actor {
  import Replica._
  import Replicator._
  import Persistence._
  import context.dispatcher

  implicit val timeout = Timeout(1 second)

  override def supervisorStrategy = AllForOneStrategy(){
    case _:PersistenceException =>
      Restart
    case _:ActorKilledException => Stop
  }

  context.system.scheduler.schedule(100 millis, 100 millis, context.self, "retry")

  var kv = Map.empty[String, String]
  // a map from secondary replicas to replicators
  var secondaries = Map.empty[ActorRef, ActorRef]
  // the current set of replicators
  var replicators = Set.empty[ActorRef]

  val persistance = context.actorOf(persistenceProps)

  var expectedSeqCounter = 0L
  var idFactory = Long.MaxValue

  var waitingAcks = Map[Long, (ActorRef, Option[Persist], Set[ActorRef])]()

  def nextId = {
    val id = idFactory
    idFactory -= 1
    id
  }
  arbiter ! Join

  def receive = {
    case JoinedPrimary   => context.become(leader)
    case JoinedSecondary => context.become(replica)
  }

  /* TODO Behavior for  the leader role. */
  val leader: Receive = {
    case command:Insert =>
      kv += (command.key -> command.value)
      doPrimaryPersist(command.key, Some(command.value), command.id)
    case command:Remove =>
      kv -= command.key
      doPrimaryPersist(command.key, None, command.id)
    case command:Get =>
      sender ! GetResult(command.key, kv.get(command.key), command.id)
    case ack:Persisted =>
      waitingAcks.get(ack.id).map{entry=>
        val (primary, _, replicatorsAcks) = entry
        waitingAcks += ack.id ->(primary, None, replicatorsAcks)
        sendAckIfOk(ack.id)
      }
    case ack:Replicated =>
      waitingAcks.get(ack.id).map{entry=>
        val (primary, command, replicatorsAcks) = entry
        waitingAcks += ack.id ->(primary, command, replicatorsAcks + sender)
        sendAckIfOk(ack.id)
      }

    case "retry" =>
      waitingAcks.foreach{entry=>
        val (_, (_, command, _)) = entry
        command.map(cmd=>persistance ! cmd)
      }

    case id:Long =>
      sendAckIfOk(id)
      waitingAcks.get(id).map{entry=>
        entry._1 ! OperationFailed(id)
      }
      waitingAcks -= id

    case replicas:Replicas =>
      var updatedReplicators = Set.empty[ActorRef]
      secondaries = replicas.replicas.foldLeft(Map.empty[ActorRef, ActorRef]){(res, replica)=>
        if (!replica.equals(self)){
          val replicator = secondaries.getOrElse(replica, context.actorOf(Props(classOf[Replicator], replica)))

          if (!secondaries.contains(replica)){
            kv.foreach{entry=>
              replicator ! Replicate(entry._1, Some(entry._2), nextId)
            }
          }
          updatedReplicators += replicator
          replicators -= replicator
          res + (replica -> replicator)
        }
        else
          res
      }

      replicators.foreach(context.stop)
      replicators = updatedReplicators

      waitingAcks.keySet.foreach(sendAckIfOk)
  }

  def doPrimaryPersist(key:String, valueOpt:Option[String], id:Long){
    val persistCommand = Persist(key, valueOpt, id)
    waitingAcks += (id -> (sender, Some(persistCommand), Set()))
    persistance ! persistCommand

    replicators.foreach(_ ! Replicate(key, valueOpt, id))

    context.system.scheduler.scheduleOnce(1000 millis, context.self, id)
  }

  def sendAckIfOk(id:Long){
    waitingAcks.get(id).map{entry=>
      val (primary, command, replicatorsAcks) = entry
      command match {
        case None =>
          if (replicators.forall{replicator=>replicatorsAcks.contains(replicator)}){
            primary ! OperationAck(id)
            waitingAcks -= id
          }
        case _ =>
      }
    }
  }
  
  /* TODO Behavior for the replica role. */
  val replica: Receive = {
    case command:Get =>
      sender ! GetResult(command.key, kv.get(command.key), command.id)
    case Snapshot(key, valueOpt, seqCounter) if seqCounter == expectedSeqCounter =>
      valueOpt match {
        case Some(value) => kv += (key -> value)
        case None => kv -= key
      }
      val persistCommand = Persist(key, valueOpt, seqCounter)
      waitingAcks += (seqCounter -> (sender, Some(persistCommand), Set()))
      persistance ! persistCommand
      
    case Snapshot(key, valueOpt, seqCounter) if seqCounter < expectedSeqCounter =>
      sender ! SnapshotAck(key, seqCounter)

    case ack:Persisted =>
      waitingAcks.get(ack.id).map{entry=>
        val (requester, command, _) = entry
        command.map(cmd=>requester ! SnapshotAck(ack.key, cmd.id))
        expectedSeqCounter += 1
      }
      waitingAcks -= ack.id
    case "retry" =>
      waitingAcks.foreach{entry=>
        val (id, (primary, command, _)) = entry
        command.map(cmd=>persistance ! cmd)
      }
  }
}


