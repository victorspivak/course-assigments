package forcomp

object ttt {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(73); 
  println("Welcome to the Scala worksheet");$skip(153); 
  
 def fd1(u:Option[String], p:Option[String], d:Option[String]) =
    for {   user <- u
            password <- p
            domain <- d}
    yield 5;System.out.println("""fd1: (u: Option[String], p: Option[String], d: Option[String])Option[Int]""");$skip(34); val res$0 = 
fd1(Some(""), Some(""), Some(""));System.out.println("""res0: Option[Int] = """ + $show(res$0))}


}
