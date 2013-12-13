package recfun
import common._
import scala.annotation.tailrec

object Main {
  def main(args: Array[String]) {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(pascal(col, row) + " ")
      println()
    }
  }

  /**
   * Exercise 1
   */
  def pascal(c: Int, r: Int): Int = {
    if (c == 0 || c == r) 1
    else pascal(c - 1, r - 1) + pascal(c, r - 1)
  }
  
  /**
   * Exercise 2
   */
  def balance(chars: List[Char]): Boolean = { 
    @tailrec def iter(level:Int, chars:List[Char]) : Boolean = {
      if (chars.isEmpty) level == 0
      else {
        if (chars.head == '(') iter(level + 1, chars.tail)
        else if (chars.head != ')') iter(level, chars.tail)
        else if (level > 0) iter(level - 1, chars.tail)
        else false
      }
    }
    iter(0, chars)
  }

  /**
   * Exercise 3
   */
  def countChange(money: Int, coins: List[Int]): Int = {
	def iter(money: Int, coins: List[Int], count:Int): Int = {
	    if (coins.isEmpty) count
	    else if (money - coins.head == 0) iter(money, coins.tail, count + 1)
		else if (money - coins.head < 0) iter(money, coins.tail, count)
		else iter(money, coins.tail, iter(money - coins.head, coins, count))
	}
	
	iter(money, coins, 0)
  }
}
