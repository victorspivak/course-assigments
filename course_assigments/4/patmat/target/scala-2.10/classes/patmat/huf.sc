package patmat
import patmat.Huffman._

object huf {
decode(frenchCode, secret)                        //> res0: List[Char] = List(h, u, f, f, m, a, n, e, s, t, c, o, o, l)

val chars = string2Chars("Hello World")           //> chars  : List[Char] = List(H, e, l, l, o,  , W, o, r, l, d)
 
                                                  
val ct = createCodeTree(chars)                    //> ct  : patmat.Huffman.CodeTree = Fork(Fork(Fork(Fork(Fork(Fork(Fork(Leaf(d,1)
                                                  //| ,Leaf(W,1),List(d, W),2),Leaf(e,1),List(d, W, e),3),Leaf(H,1),List(d, W, e, 
                                                  //| H),4),Leaf(r,1),List(d, W, e, H, r),5),Leaf( ,1),List(d, W, e, H, r,  ),6),L
                                                  //| eaf(o,2),List(d, W, e, H, r,  , o),8),Leaf(l,3),List(d, W, e, H, r,  , o, l)
                                                  //| ,11)
 
val sec = encode(ct)(chars)                       //> sec  : List[patmat.Huffman.Bit] = List(0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1
                                                  //| , 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0,
                                                  //|  0)
decode(ct, sec)                                   //> res1: List[Char] = List(H, e, l, l, o,  , W, o, r, l, d)

decode (ct, List())                               //> res2: List[Char] = List()
 
convert(ct)                                       //> res3: patmat.Huffman.CodeTable = List((d,List(0, 0, 0, 0, 0, 0, 0)), (W,List
                                                  //| (0, 0, 0, 0, 0, 0, 1)), (e,List(0, 0, 0, 0, 0, 1)), (H,List(0, 0, 0, 0, 1)),
                                                  //|  (r,List(0, 0, 0, 1)), ( ,List(0, 0, 1)), (o,List(0, 1)), (l,List(1)))
val sec1 = quickEncode(ct)(chars)                 //> sec1  : List[patmat.Huffman.Bit] = List(0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 
                                                  //| 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0
                                                  //| , 0)
decode(ct, sec1)                                  //> res4: List[Char] = List(H, e, l, l, o,  , W, o, r, l, d)


}