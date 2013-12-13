package objsets

object test1 {
    val set1 = new Empty                          //> set1  : objsets.Empty = .
    val set2 = set1.incl(new Tweet("a", "a body", 20))
                                                  //> set2  : objsets.TweetSet = {.a.}
    val set3 = set2.incl(new Tweet("b", "b body", 20))
                                                  //> set3  : objsets.TweetSet = {.a{.b.}}
    val c = new Tweet("c", "c body", 7)           //> c  : objsets.Tweet = User: c
                                                  //| Text: c body [7]
    val d = new Tweet("d", "d body", 9)           //> d  : objsets.Tweet = User: d
                                                  //| Text: d body [9]
    val set4c = set3.incl(c)                      //> set4c  : objsets.TweetSet = {.a{.b{.c.}}}
    val set4d = set3.incl(d)                      //> set4d  : objsets.TweetSet = {.a{.b{.d.}}}
      val set5 = set4c.incl(d)                    //> set5  : objsets.TweetSet = {.a{.b{.c{.d.}}}}
 
 val s1 = set5.filter{tw => println(tw.user + " " + (tw.user == "a")); tw.user == "a"}
                                                  //> a true
                                                  //| b false
                                                  //| c false
                                                  //| d false
                                                  //| s1  : objsets.TweetSet = {.a.}
//l1.foreach(e => println(e))
def filter(words:List[String])(tweet:Tweet):Boolean  = {
    if (words.isEmpty) false
    else if (tweet.text.contains(words.head)) true
    else filter(words.tail)(tweet)
  }                                               //> filter: (words: List[String])(tweet: objsets.Tweet)Boolean
  
val t1 = set5.filter(tweet => filter(List("b body", "c body"))(tweet))
                                                  //> t1  : objsets.TweetSet = {.b{.c.}}
println(t1)                                       //> {.b{.c.}}
}