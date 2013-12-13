package funsets

object FunSetsWS {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  
  type Set = Int => Boolean
	def contains(s: Set, elem: Int): Boolean = s(elem)
                                                  //> contains: (s: Int => Boolean, elem: Int)Boolean
  
  def singletonSet(elem: Int): Set = toTest => elem == toTest
                                                  //> singletonSet: (elem: Int)Int => Boolean
  contains(singletonSet(1), 2)                    //> res0: Boolean = false
 
  def union(s: Set, t: Set): Set = toTest => s(toTest) || t(toTest)
                                                  //> union: (s: Int => Boolean, t: Int => Boolean)Int => Boolean
  
  val set12 = union(singletonSet(1), singletonSet(2))
                                                  //> set12  : Int => Boolean = <function1>
  contains(set12, 1)                              //> res1: Boolean = true
  contains(set12, 2)                              //> res2: Boolean = true
  contains(set12, 3)                              //> res3: Boolean = false
  
  def intersect(s: Set, t: Set): Set = toTest => s(toTest) && t(toTest)
                                                  //> intersect: (s: Int => Boolean, t: Int => Boolean)Int => Boolean
  val set1 = intersect(set12, singletonSet(1))    //> set1  : Int => Boolean = <function1>
  contains(set1, 1)                               //> res4: Boolean = true
  contains(set1, 2)                               //> res5: Boolean = false
  contains(set1, 3)                               //> res6: Boolean = false
  set1(1)                                         //> res7: Boolean = true
  set1(2)                                         //> res8: Boolean = false
  
  def diff(s: Set, t: Set): Set = toTest => s(toTest) && !t(toTest)
                                                  //> diff: (s: Int => Boolean, t: Int => Boolean)Int => Boolean
  val dif1 = diff(set12, set1)                    //> dif1  : Int => Boolean = <function1>
  dif1(1)                                         //> res9: Boolean = false
  dif1(2)                                         //> res10: Boolean = true
  dif1(3)                                         //> res11: Boolean = false
  
  def filter(s: Set, p: Int => Boolean): Set = toTest => s(toTest) && p(toTest)
                                                  //> filter: (s: Int => Boolean, p: Int => Boolean)Int => Boolean
  
  filter(set12, e => e > 1)(1)                    //> res12: Boolean = false
  filter(set12, e => e > 1)(2)                    //> res13: Boolean = true
  filter(set12, e => e > 1)(3)                    //> res14: Boolean = false
  
  val bound = 1000                                //> bound  : Int = 1000
  def forall(s: Set, p: Int => Boolean): Boolean = {
    def iter(a: Int): Boolean = {
      if (a > 1000) true
      else if (s(a) && !p(a)) false
      else iter(a + 1)
    }
    iter(-bound)
 }                                                //> forall: (s: Int => Boolean, p: Int => Boolean)Boolean
  
 forall(set12, e => e > -1)                       //> res15: Boolean = true
 forall(set12, e => e > 3)                        //> res16: Boolean = false
 
   def toString(s: Set): String = {
    val xs = for (i <- -bound to bound if contains(s, i)) yield i
    xs.mkString("{", ",", "}")
  }                                               //> toString: (s: Int => Boolean)String

  /**
   * Prints the contents of a set on the console.
   */
  def printSet(s: Set) {
    println(toString(s))
  }                                               //> printSet: (s: Int => Boolean)Unit
 
 printSet(set12)                                  //> {1,2}
 printSet(set1)                                   //> {1}
 printSet(dif1)                                   //> {2}
}