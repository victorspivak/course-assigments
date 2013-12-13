package streams

object test {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  def from(n:Int):Stream[Int] = n#:: from(n+1)    //> from: (n: Int)Stream[Int]
  val nats = from(0)                              //> nats  : Stream[Int] = Stream(0, ?)
  nats.map(_ * 3).take(10).toList                 //> res0: List[Int] = List(0, 3, 6, 9, 12, 15, 18, 21, 24, 27)
  
  def sieve(s: Stream[Int]): Stream[Int] = s.head #:: sieve(s.tail filter(_ % s.head != 0))
                                                  //> sieve: (s: Stream[Int])Stream[Int]
  val primes = sieve(from(2))                     //> primes  : Stream[Int] = Stream(2, ?)
  primes.take(10).toList                          //> res1: List[Int] = List(2, 3, 5, 7, 11, 13, 17, 19, 23, 29)
  
  val s = Set(1,2,3)                              //> s  : scala.collection.immutable.Set[Int] = Set(1, 2, 3)
  
  s + 10 + 20 + 30                                //> res2: scala.collection.immutable.Set[Int] = Set(10, 20, 1, 2, 3, 30)
  
  
  
}