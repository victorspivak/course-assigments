package objsets

object test1 {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(56); 
    val set1 = new Empty;System.out.println("""set1  : objsets.Empty = """ + $show(set1 ));$skip(55); 
    val set2 = set1.incl(new Tweet("a", "a body", 20));System.out.println("""set2  : objsets.TweetSet = """ + $show(set2 ));$skip(55); 
    val set3 = set2.incl(new Tweet("b", "b body", 20));System.out.println("""set3  : objsets.TweetSet = """ + $show(set3 ));$skip(40); 
    val c = new Tweet("c", "c body", 7);System.out.println("""c  : objsets.Tweet = """ + $show(c ));$skip(40); 
    val d = new Tweet("d", "d body", 9);System.out.println("""d  : objsets.Tweet = """ + $show(d ));$skip(29); 
    val set4c = set3.incl(c);System.out.println("""set4c  : objsets.TweetSet = """ + $show(set4c ));$skip(29); 
    val set4d = set3.incl(d);System.out.println("""set4d  : objsets.TweetSet = """ + $show(set4d ));$skip(31); 
      val set5 = set4c.incl(d);System.out.println("""set5  : objsets.TweetSet = """ + $show(set5 ));$skip(89); 
 
 val s1 = set5.filter{tw => println(tw.user + " " + (tw.user == "a")); tw.user == "a"};System.out.println("""s1  : objsets.TweetSet = """ + $show(s1 ));$skip(206); 
//l1.foreach(e => println(e))
def filter(words:List[String])(tweet:Tweet):Boolean  = {
    if (words.isEmpty) false
    else if (tweet.text.contains(words.head)) true
    else filter(words.tail)(tweet)
  };System.out.println("""filter: (words: List[String])(tweet: objsets.Tweet)Boolean""");$skip(74); 
  
val t1 = set5.filter(tweet => filter(List("b body", "c body"))(tweet));System.out.println("""t1  : objsets.TweetSet = """ + $show(t1 ));$skip(12); 
println(t1)}
}
