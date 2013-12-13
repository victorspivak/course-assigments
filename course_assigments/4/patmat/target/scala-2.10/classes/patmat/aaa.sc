package patmat

import Huffman._

object aaa {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  val text = string2Chars("abcde1234567890")      //> text  : List[Char] = List(a, b, c, d, e, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0)
  val tree = createCodeTree(text)                 //> tree  : patmat.Huffman.CodeTree = Fork(Fork(Fork(Leaf(9,1),Fork(Leaf(5,1),Le
                                                  //| af(7,1),List(5, 7),2),List(9, 5, 7),3),Fork(Fork(Leaf(6,1),Leaf(4,1),List(6,
                                                  //|  4),2),Fork(Leaf(0,1),Leaf(8,1),List(0, 8),2),List(6, 4, 0, 8),4),List(9, 5,
                                                  //|  7, 6, 4, 0, 8),7),Fork(Fork(Fork(Leaf(c,1),Leaf(a,1),List(c, a),2),Fork(Lea
                                                  //| f(2,1),Leaf(e,1),List(2, e),2),List(c, a, 2, e),4),Fork(Fork(Leaf(1,1),Leaf(
                                                  //| 3,1),List(1, 3),2),Fork(Leaf(b,1),Leaf(d,1),List(b, d),2),List(1, 3, b, d),4
                                                  //| ),List(c, a, 2, e, 1, 3, b, d),8),List(9, 5, 7, 6, 4, 0, 8, c, a, 2, e, 1, 3
                                                  //| , b, d),15)
  val table = convert(tree)                       //> table  : patmat.Huffman.CodeTable = List((9,List(0, 0, 0)), (5,List(0, 0, 1,
                                                  //|  0)), (7,List(0, 0, 1, 1)), (6,List(0, 1, 0, 0)), (4,List(0, 1, 0, 1)), (0,L
                                                  //| ist(0, 1, 1, 0)), (8,List(0, 1, 1, 1)), (c,List(1, 0, 0, 0)), (a,List(1, 0, 
                                                  //| 0, 1)), (2,List(1, 0, 1, 0)), (e,List(1, 0, 1, 1)), (1,List(1, 1, 0, 0)), (3
                                                  //| ,List(1, 1, 0, 1)), (b,List(1, 1, 1, 0)), (d,List(1, 1, 1, 1)))
  quickEncode(tree)(text).size                    //> res0: Int = 59
}