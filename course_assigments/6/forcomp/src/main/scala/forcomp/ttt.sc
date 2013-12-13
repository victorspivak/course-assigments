package forcomp

object ttt {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  
 def fd1(u:Option[String], p:Option[String], d:Option[String]) =
    for {   user <- u
            password <- p
            domain <- d}
    yield 5                                       //> fd1: (u: Option[String], p: Option[String], d: Option[String])Option[Int]
fd1(Some(""), Some(""), Some(""))                 //> res0: Option[Int] = Some(5)


}