package funsets

object FunSetsWS {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(79); 
  println("Welcome to the Scala worksheet")
  
  type Set = Int => Boolean;$skip(83); 
	def contains(s: Set, elem: Int): Boolean = s(elem);System.out.println("""contains: (s: Int => Boolean, elem: Int)Boolean""");$skip(65); 
  
  def singletonSet(elem: Int): Set = toTest => elem == toTest;System.out.println("""singletonSet: (elem: Int)Int => Boolean""");$skip(31); val res$0 = 
  contains(singletonSet(1), 2);System.out.println("""res0: Boolean = """ + $show(res$0));$skip(70); 
 
  def union(s: Set, t: Set): Set = toTest => s(toTest) || t(toTest);System.out.println("""union: (s: Int => Boolean, t: Int => Boolean)Int => Boolean""");$skip(57); 
  
  val set12 = union(singletonSet(1), singletonSet(2));System.out.println("""set12  : Int => Boolean = """ + $show(set12 ));$skip(21); val res$1 = 
  contains(set12, 1);System.out.println("""res1: Boolean = """ + $show(res$1));$skip(21); val res$2 = 
  contains(set12, 2);System.out.println("""res2: Boolean = """ + $show(res$2));$skip(21); val res$3 = 
  contains(set12, 3);System.out.println("""res3: Boolean = """ + $show(res$3));$skip(75); 
  
  def intersect(s: Set, t: Set): Set = toTest => s(toTest) && t(toTest);System.out.println("""intersect: (s: Int => Boolean, t: Int => Boolean)Int => Boolean""");$skip(47); 
  val set1 = intersect(set12, singletonSet(1));System.out.println("""set1  : Int => Boolean = """ + $show(set1 ));$skip(20); val res$4 = 
  contains(set1, 1);System.out.println("""res4: Boolean = """ + $show(res$4));$skip(20); val res$5 = 
  contains(set1, 2);System.out.println("""res5: Boolean = """ + $show(res$5));$skip(20); val res$6 = 
  contains(set1, 3);System.out.println("""res6: Boolean = """ + $show(res$6));$skip(10); val res$7 = 
  set1(1);System.out.println("""res7: Boolean = """ + $show(res$7));$skip(10); val res$8 = 
  set1(2);System.out.println("""res8: Boolean = """ + $show(res$8));$skip(71); 
  
  def diff(s: Set, t: Set): Set = toTest => s(toTest) && !t(toTest);System.out.println("""diff: (s: Int => Boolean, t: Int => Boolean)Int => Boolean""");$skip(31); 
  val dif1 = diff(set12, set1);System.out.println("""dif1  : Int => Boolean = """ + $show(dif1 ));$skip(10); val res$9 = 
  dif1(1);System.out.println("""res9: Boolean = """ + $show(res$9));$skip(10); val res$10 = 
  dif1(2);System.out.println("""res10: Boolean = """ + $show(res$10));$skip(10); val res$11 = 
  dif1(3);System.out.println("""res11: Boolean = """ + $show(res$11));$skip(83); 
  
  def filter(s: Set, p: Int => Boolean): Set = toTest => s(toTest) && p(toTest);System.out.println("""filter: (s: Int => Boolean, p: Int => Boolean)Int => Boolean""");$skip(34); val res$12 = 
  
  filter(set12, e => e > 1)(1);System.out.println("""res12: Boolean = """ + $show(res$12));$skip(31); val res$13 = 
  filter(set12, e => e > 1)(2);System.out.println("""res13: Boolean = """ + $show(res$13));$skip(31); val res$14 = 
  filter(set12, e => e > 1)(3);System.out.println("""res14: Boolean = """ + $show(res$14));$skip(22); 
  
  val bound = 1000;System.out.println("""bound  : Int = """ + $show(bound ));$skip(197); 
  def forall(s: Set, p: Int => Boolean): Boolean = {
    def iter(a: Int): Boolean = {
      if (a > 1000) true
      else if (s(a) && !p(a)) false
      else iter(a + 1)
    }
    iter(-bound)
 };System.out.println("""forall: (s: Int => Boolean, p: Int => Boolean)Boolean""");$skip(31); val res$15 = 
  
 forall(set12, e => e > -1);System.out.println("""res15: Boolean = """ + $show(res$15));$skip(27); val res$16 = 
 forall(set12, e => e > 3);System.out.println("""res16: Boolean = """ + $show(res$16));$skip(139); 
 
   def toString(s: Set): String = {
    val xs = for (i <- -bound to bound if contains(s, i)) yield i
    xs.mkString("{", ",", "}")
  };System.out.println("""toString: (s: Int => Boolean)String""");$skip(117); 

  /**
   * Prints the contents of a set on the console.
   */
  def printSet(s: Set) {
    println(toString(s))
  };System.out.println("""printSet: (s: Int => Boolean)Unit""");$skip(19); 
 
 printSet(set12);$skip(16); 
 printSet(set1);$skip(16); 
 printSet(dif1)}
}
