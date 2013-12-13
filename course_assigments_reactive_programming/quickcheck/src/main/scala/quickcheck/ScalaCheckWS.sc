package quickcheck

import sun.org.mozilla.javascript.internal.ast.Yield

object ScalaCheckWS {
  import org.scalacheck._
	import Gen._
  
  List(5, 8, 3).sortWith(_ < _)                   //> res0: List[Int] = List(3, 5, 8)
  
  val myGen      = Gen.containerOf[List,Boolean](Gen.frequency((10, true),(1, false)))
                                                  //> myGen  : org.scalacheck.Gen[List[Boolean]] = Gen()
	myGen.sample                              //> res1: Option[List[Boolean]] = Some(List(true, true, true, true, true, true, 
                                                  //| true, true, false, true, true, false, true, true, true, true, true, true, tr
                                                  //| ue, true, true, true, true, true, true, true, true, true, true, true, true, 
                                                  //| true, false, false, true, true, true, true, true, false, true, true, true, t
                                                  //| rue, true, true, true, true, false, true, true, true))
	
	Arbitrary(myGen).arbitrary                //> res2: org.scalacheck.Gen[List[Boolean]] = Gen()
	type T = Int
	type H = List[T]
	
	def emptyHeap:Gen[H] = List()             //> emptyHeap: => org.scalacheck.Gen[quickcheck.ScalaCheckWS.H]
	
	def nonEptyHeap:Gen[H] = for{
		v <- Arbitrary.arbitrary[T]
		tail <- heapGen
	} yield(v :: tail)                        //> nonEptyHeap: => org.scalacheck.Gen[quickcheck.ScalaCheckWS.H]
		
	def heapGen:Gen[H] = for {
		isEmpty <- Gen.frequency((1, true),(10, false))
		heap <- if (isEmpty)
							emptyHeap
						else
							nonEptyHeap
	}	yield(heap)                       //> heapGen: => org.scalacheck.Gen[quickcheck.ScalaCheckWS.H]
	
	
	heapGen.sample                            //> res3: Option[quickcheck.ScalaCheckWS.H] = Some(List(-2147483648, -1459555198
                                                  //| , 29929358, -266741132, 1662092575, 570236203, -2147483648, 1087866995, 0, -
                                                  //| 2147483648, 2128302711))
	
	import quickcheck.Bogus4BinomialHeap
	
	val hp = new Bogus3BinomialHeap with IntHeap
                                                  //> hp  : quickcheck.Bogus3BinomialHeap with quickcheck.IntHeap = quickcheck.Sca
                                                  //| laCheckWS$$anonfun$main$1$$anon$1@11ef9f6
	val h1 = hp.insert(10, hp.empty)          //> h1  : quickcheck.ScalaCheckWS.hp.H = List(Node(10,0,List()))
	val h2 = hp.insert(20, h1)                //> h2  : quickcheck.ScalaCheckWS.hp.H = List(Node(10,1,List(Node(10,0,List())))
                                                  //| )
	val h3 = hp.insert(30, h2)                //> h3  : quickcheck.ScalaCheckWS.hp.H = List(Node(30,0,List()), Node(10,1,List(
                                                  //| Node(10,0,List()))))
	val h4 = hp.insert(40, h3)                //> h4  : quickcheck.ScalaCheckWS.hp.H = List(Node(10,2,List(Node(10,1,List(Node
                                                  //| (10,0,List()))), Node(10,0,List()))))
	val h5 = hp.insert(50, h4)                //> h5  : quickcheck.ScalaCheckWS.hp.H = List(Node(50,0,List()), Node(10,2,List(
                                                  //| Node(10,1,List(Node(10,0,List()))), Node(10,0,List()))))
 hp.deleteMin(h1)                                 //> res4: quickcheck.ScalaCheckWS.hp.H = List()
 hp.deleteMin(h2)                                 //> res5: quickcheck.ScalaCheckWS.hp.H = List(Node(10,0,List()))
 hp.deleteMin(h3)                                 //> res6: quickcheck.ScalaCheckWS.hp.H = List(Node(10,1,List(Node(10,0,List())))
                                                  //| )
 hp.deleteMin(h4)                                 //> res7: quickcheck.ScalaCheckWS.hp.H = List(Node(10,0,List()), Node(10,1,List(
                                                  //| Node(10,0,List()))))
 hp.deleteMin(h5)                                 //> res8: quickcheck.ScalaCheckWS.hp.H = List(Node(10,2,List(Node(10,1,List(Node
                                                  //| (10,0,List()))), Node(10,0,List()))))
}