package quickcheck

import sun.org.mozilla.javascript.internal.ast.Yield

object ScalaCheckWS {
  import org.scalacheck._
	import Gen._;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(171); val res$0 = 
  
  List(5, 8, 3).sortWith(_ < _);System.out.println("""res0: List[Int] = """ + $show(res$0));$skip(90); 
  
  val myGen      = Gen.containerOf[List,Boolean](Gen.frequency((10, true),(1, false)));System.out.println("""myGen  : org.scalacheck.Gen[List[Boolean]] = """ + $show(myGen ));$skip(14); val res$1 = 
	myGen.sample;System.out.println("""res1: Option[List[Boolean]] = """ + $show(res$1));$skip(30); val res$2 = 
	
	Arbitrary(myGen).arbitrary
	type T = Int
	type H = List[T];System.out.println("""res2: org.scalacheck.Gen[List[Boolean]] = """ + $show(res$2));$skip(65); 
	
	def emptyHeap:Gen[H] = List();System.out.println("""emptyHeap: => org.scalacheck.Gen[quickcheck.ScalaCheckWS.H]""");$skip(101); 
	
	def nonEptyHeap:Gen[H] = for{
		v <- Arbitrary.arbitrary[T]
		tail <- heapGen
	} yield(v :: tail);System.out.println("""nonEptyHeap: => org.scalacheck.Gen[quickcheck.ScalaCheckWS.H]""");$skip(166); 
		
	def heapGen:Gen[H] = for {
		isEmpty <- Gen.frequency((1, true),(10, false))
		heap <- if (isEmpty)
							emptyHeap
						else
							nonEptyHeap
	}	yield(heap);System.out.println("""heapGen: => org.scalacheck.Gen[quickcheck.ScalaCheckWS.H]""");$skip(20); val res$3 = 
	
	
	heapGen.sample
	
	import quickcheck.Bogus4BinomialHeap;System.out.println("""res3: Option[quickcheck.ScalaCheckWS.H] = """ + $show(res$3));$skip(88); 
	
	val hp = new Bogus3BinomialHeap with IntHeap;System.out.println("""hp  : quickcheck.Bogus3BinomialHeap with quickcheck.IntHeap = """ + $show(hp ));$skip(34); 
	val h1 = hp.insert(10, hp.empty);System.out.println("""h1  : quickcheck.ScalaCheckWS.hp.H = """ + $show(h1 ));$skip(28); 
	val h2 = hp.insert(20, h1);System.out.println("""h2  : quickcheck.ScalaCheckWS.hp.H = """ + $show(h2 ));$skip(28); 
	val h3 = hp.insert(30, h2);System.out.println("""h3  : quickcheck.ScalaCheckWS.hp.H = """ + $show(h3 ));$skip(28); 
	val h4 = hp.insert(40, h3);System.out.println("""h4  : quickcheck.ScalaCheckWS.hp.H = """ + $show(h4 ));$skip(28); 
	val h5 = hp.insert(50, h4);System.out.println("""h5  : quickcheck.ScalaCheckWS.hp.H = """ + $show(h5 ));$skip(18); val res$4 = 
 hp.deleteMin(h1);System.out.println("""res4: quickcheck.ScalaCheckWS.hp.H = """ + $show(res$4));$skip(18); val res$5 = 
 hp.deleteMin(h2);System.out.println("""res5: quickcheck.ScalaCheckWS.hp.H = """ + $show(res$5));$skip(18); val res$6 = 
 hp.deleteMin(h3);System.out.println("""res6: quickcheck.ScalaCheckWS.hp.H = """ + $show(res$6));$skip(18); val res$7 = 
 hp.deleteMin(h4);System.out.println("""res7: quickcheck.ScalaCheckWS.hp.H = """ + $show(res$7));$skip(18); val res$8 = 
 hp.deleteMin(h5);System.out.println("""res8: quickcheck.ScalaCheckWS.hp.H = """ + $show(res$8))}
}
