package streams

object test {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(74); 
  println("Welcome to the Scala worksheet");$skip(47); 
  def from(n:Int):Stream[Int] = n#:: from(n+1);System.out.println("""from: (n: Int)Stream[Int]""");$skip(21); 
  val nats = from(0);System.out.println("""nats  : Stream[Int] = """ + $show(nats ));$skip(34); val res$0 = 
  nats.map(_ * 3).take(10).toList;System.out.println("""res0: List[Int] = """ + $show(res$0));$skip(95); 
  
  def sieve(s: Stream[Int]): Stream[Int] = s.head #:: sieve(s.tail filter(_ % s.head != 0));System.out.println("""sieve: (s: Stream[Int])Stream[Int]""");$skip(30); 
  val primes = sieve(from(2));System.out.println("""primes  : Stream[Int] = """ + $show(primes ));$skip(25); val res$1 = 
  primes.take(10).toList;System.out.println("""res1: List[Int] = """ + $show(res$1));$skip(24); 
  
  val s = Set(1,2,3);System.out.println("""s  : scala.collection.immutable.Set[Int] = """ + $show(s ));$skip(22); val res$2 = 
  
  s + 10 + 20 + 30;System.out.println("""res2: scala.collection.immutable.Set[Int] = """ + $show(res$2))}
  
  
  
}
