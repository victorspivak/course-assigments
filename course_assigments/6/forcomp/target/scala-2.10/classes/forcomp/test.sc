package forcomp

import Anagrams._

object test {
  try {
   sentenceAnagrams(List("I", "love", "you"))
  } catch {
    case e => println(e)
  }
}