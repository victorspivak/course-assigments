package simulations

import common._

class Wire {
  private var sigVal = false
  private var actions: List[Simulator#Action] = List()

  def getSignal: Boolean = sigVal
  
  def setSignal(s: Boolean) {
    if (s != sigVal) {
      sigVal = s
      actions.foreach(action => action())
    }
  }

  def addAction(a: Simulator#Action) {
    actions = a :: actions
    a()
  }
}

abstract class CircuitSimulator extends Simulator {

  val InverterDelay: Int
  val AndGateDelay: Int
  val OrGateDelay: Int

  def probe(name: String, wire: Wire) {
    wire addAction {
      () => afterDelay(0) {
        println(
          "  " + currentTime + ": " + name + " -> " +  wire.getSignal)
      }
    }
  }

  def inverter(input: Wire, output: Wire) {
    def invertAction() {
      val inputSig = input.getSignal
      afterDelay(InverterDelay) { output.setSignal(!inputSig) }
    }
    input addAction invertAction
  }

  def andGate(a1: Wire, a2: Wire, output: Wire) {
    def andAction() {
      val a1Sig = a1.getSignal
      val a2Sig = a2.getSignal
      afterDelay(AndGateDelay) { output.setSignal(a1Sig & a2Sig) }
    }
    a1 addAction andAction
    a2 addAction andAction
  }

  def orGate(a1: Wire, a2: Wire, output: Wire) {
    def orAction() {
      val a1Sig = a1.getSignal
      val a2Sig = a2.getSignal
      afterDelay(OrGateDelay) { output.setSignal(a1Sig | a2Sig) }
    }
    a1 addAction orAction
    a2 addAction orAction
  }

  
  def orGate2(a1: Wire, a2: Wire, output: Wire) {
    val invA1 = new Wire
    val invA2 = new Wire
    
    inverter(a1, invA1)
    inverter(a2, invA2)
    
    val notA1AndNotA2 = new Wire
    
    andGate(invA1, invA2, notA1AndNotA2)
    
    inverter(notA1AndNotA2, output)
  }


  def demux(in: Wire, c: List[Wire], out: List[Wire]) {
	  c match {
	    case List(control) => demux12(in, control, out)
	    case head::tail => 
	      val t1, t2 = new Wire
	      val length = out.length
	      demux12(in, head, List(t1, t2))
	      
	      demux(t1, tail, out.take(length/2))
	      demux(t2, tail, out.drop(length/2))
	  }
    
	def demux12(iw:Wire, cw:Wire, o:List[Wire]) {
	  andGate(iw, cw, o.head)
	  val notC = new Wire
	  inverter(cw, notC)
	  andGate(iw, notC, o.tail.head)
	}
  }

}

object Circuit extends CircuitSimulator {
  val InverterDelay = 1
  val AndGateDelay = 3
  val OrGateDelay = 5

  def andGateExample {
    val in1, in2, out = new Wire
    andGate(in1, in2, out)
    probe("in1", in1)
    probe("in2", in2)
    probe("out", out)
    in1.setSignal(false)
    in2.setSignal(false)
    run

    in1.setSignal(true)
    run

    in2.setSignal(true)
    run
  }

  def demuxExample {
    val c, i, o1, o2 = new Wire

    probe("c", c)
    probe("i", i)
    probe("o1", o1)
    probe("o2", o2)
    
    demux(i, List(c), List(o1, o2))

    c.setSignal(false)
    i.setSignal(true)
    run

    c.setSignal(true)
    i.setSignal(true)
    run
  }
  //
  // to complete with orGateExample and demuxExample...
  //
}

object CircuitMain extends App {
  // You can write tests either here, or better in the test class CircuitSuite.
  Circuit.demuxExample
}
