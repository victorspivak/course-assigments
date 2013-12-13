package forcomp

import Anagrams._

object test {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(145); val res$0 = 
  try {
   sentenceAnagrams(List("I", "love", "you"))
  } catch {
    case e => println(e)
  };System.out.println("""res0: Any = """ + $show(res$0))}
}
