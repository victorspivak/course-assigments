package simulations

object Sim {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(77); 
  println("Welcome to the Scala worksheet");$skip(20); 
  val a1 = new Wire;System.out.println("""a1  : simulations.Wire = """ + $show(a1 ));$skip(20); 
  val a2 = new Wire;System.out.println("""a2  : simulations.Wire = """ + $show(a2 ));$skip(24); 
  
  a1.setSignal(true)}
  
  
}
