package patmat
import patmat.Huffman._

object huf {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(79); val res$0 = 
decode(frenchCode, secret);System.out.println("""res0: List[Char] = """ + $show(res$0));$skip(41); 

val chars = string2Chars("Hello World");System.out.println("""chars  : List[Char] = """ + $show(chars ));$skip(84); 
 
                                                  
val ct = createCodeTree(chars);System.out.println("""ct  : patmat.Huffman.CodeTree = """ + $show(ct ));$skip(30); 
 
val sec = encode(ct)(chars);System.out.println("""sec  : List[patmat.Huffman.Bit] = """ + $show(sec ));$skip(16); val res$1 = 
decode(ct, sec);System.out.println("""res1: List[Char] = """ + $show(res$1));$skip(21); val res$2 = 

decode (ct, List());System.out.println("""res2: List[Char] = """ + $show(res$2));$skip(14); val res$3 = 
 
convert(ct);System.out.println("""res3: patmat.Huffman.CodeTable = """ + $show(res$3));$skip(34); 
val sec1 = quickEncode(ct)(chars);System.out.println("""sec1  : List[patmat.Huffman.Bit] = """ + $show(sec1 ));$skip(17); val res$4 = 
decode(ct, sec1);System.out.println("""res4: List[Char] = """ + $show(res$4))}


}
