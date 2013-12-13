package quickcheck

import common._

import org.scalacheck._
import Arbitrary._
import Gen._
import Prop._
import scala.math._

abstract class QuickCheckHeap extends Properties("Heap") with IntHeap {

  property("min1") = forAll { a: Int =>
    val h = insert(a, empty)
    findMin(h) == a
  }

  property("min2") = forAll { (a1: Int, a2:Int, a3:Int) =>
    val h = insert(a1, insert(a2, insert(a3, empty)))
    findMin(h) == min(min(a1, a2), a3)
  }

  property("min3") = forAll {(h:H, a:Int) =>
    val m = findMin(h)
    val newMin = m - a
    if (newMin < m){
	    val updated = insert(newMin, h)
	    findMin(updated) == newMin
    }
    else
      true
  }

  property("min4") = forAll { (a1: Int, a2:Int, a3:Int) =>
    def checkMin(elements:List[Int], h:H):Boolean = {
      if (elements.isEmpty && isEmpty(h))
        true
      else if (elements.isEmpty || isEmpty(h))
        false
      else{
        if (findMin(h) == elements.head)
          checkMin(elements.tail, deleteMin(h))
        else
          false
      }
    }
    
    val h = insert(a1, insert(a2, insert(a3, empty)))
    val elements = List(a1, a2, a3).sortWith(_ < _)
    checkMin(elements, h)
  }

  property("deleteMin1") = forAll { a: Int =>
    val h = insert(a, empty)
    val shouldBeEmpty = deleteMin(h)
    isEmpty(shouldBeEmpty)
  }

  property("deleteMin2") = forAll { (a1: Int, a2:Int, a3:Int) =>
    val h = insert(a1, insert(a2, insert(a3, empty)))
    val shouldBeEmpty = deleteMin(deleteMin(deleteMin(h)))
    isEmpty(shouldBeEmpty)
  }

  property("deleteMin3") = forAll { h:H =>
    val min1 = findMin(h)
    val updated = deleteMin(h)
    if (!isEmpty(updated))
    	findMin(updated) >= min1
    else
      true
  }
  
  property("meld1") = forAll { (h1:H, h2:H) =>
    val merged = meld(h1, h2)
    val min = findMin(merged)
    min == findMin(h1) || min == findMin(h2)
  }

  property("meld2") = forAll { h:H =>
    val merged = meld(h, empty)
    findMin(h) == findMin(merged)
  }
  
  property("meld3") = forAll { h:H =>
    val merged = meld(empty, h)
    findMin(h) == findMin(merged)
  }
  

  def emptyHeap:Gen[H] = empty
  def nonEmptyHeap:Gen[H] = for{
		v <- Arbitrary.arbitrary[A]
		heap <- genHeapImp
	} yield(insert(v, heap))
	
  def genHeapImp: Gen[H] = for {
		isEmpty <- Gen.frequency((1, true),(10, false))
		heap <- if (isEmpty)
					emptyHeap
				else
					nonEmptyHeap
	}	yield(heap)   
	
	
  lazy val genHeap: Gen[H] = nonEmptyHeap 

  implicit lazy val arbHeap: Arbitrary[H] = Arbitrary(genHeap)

		
}
