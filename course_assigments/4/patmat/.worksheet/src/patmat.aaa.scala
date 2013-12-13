package patmat

import Huffman._

object aaa {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(90); 
  println("Welcome to the Scala worksheet");$skip(45); 
  val text = string2Chars("abcde1234567890");System.out.println("""text  : List[Char] = """ + $show(text ));$skip(34); 
  val tree = createCodeTree(text);System.out.println("""tree  : patmat.Huffman.CodeTree = """ + $show(tree ));$skip(28); 
  val table = convert(tree);System.out.println("""table  : patmat.Huffman.CodeTable = """ + $show(table ));$skip(31); val res$0 = 
  quickEncode(tree)(text).size;System.out.println("""res0: Int = """ + $show(res$0))}
}
