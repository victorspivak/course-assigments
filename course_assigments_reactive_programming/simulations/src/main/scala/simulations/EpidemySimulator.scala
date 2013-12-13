package simulations

import math.random


class EpidemySimulator extends Simulator {

  def randomBelow(i: Int) = (random * i).toInt

  protected[simulations] object SimConfig {
    val population: Int = 300
    val roomRows: Int = 8
    val roomColumns: Int = 8
    
    val mobility = 5
    //the following numbers in percents
    val transmisibilityRate = 40
    val prevalenceRate = 1
    val deathRate = 25

    val airTrafficRate = 0
    val vipRate = 0
    val mobilityAct = false
    val mobilityActHealthPersonRatio = 2
    val mobilityActSickPersonRatio = 4
    
    val inf2SickPeriod = 6
    val inf2DiePeriod = 14
    val inf2ImmunePeriod = 16
    val inf2HealtyPeriod = 18

    // to complete: additional parameters of simulation
  }

  import SimConfig._

  val initInfCount = population * prevalenceRate / 100 
  val vipCount = initInfCount + population * vipRate / 100
  
  val persons: List[Person] = (1 to population).map{i => 
    val p = new Person(i)
    if (i <= initInfCount)
      p.becomeInfected()
    else if (i <= vipCount)
      p.vip = true
      
    p
  }.toList.reverse

  def isInfectedRoom(row:Int, col:Int) = !peopleInTheRoom(row, col).filter(_.isInfected).isEmpty
  
  def peopleInTheRoom(row:Int, col:Int) = persons.filter{p=>p.col == col && p.row == row}
  
  def findRandomRoom(row:Int, col:Int):Option[(Int, Int)] = {
    val r = randomBelow(roomRows)
    val c = randomBelow(roomColumns)
    
    if (r != row || c != col) 
      Some((r,c))
    else
      findRandomRoom(row, col)
  }
  
  def findNextRoom(row:Int, col:Int) = {
    if (airTrafficRate > 0 && randomBelow(100) < airTrafficRate) {
    	findRandomRoom(row, col)
    } else
      findNextValidRoom(row, col)
  }
    
  def findNextValidRoom(row:Int, col:Int) = findValidNeighbourRooms(row, col) match {
    case List() => None
    case candidates => Some(candidates(randomBelow(candidates.length)))
  }
    
  def findValidNeighbourRooms(row:Int, col:Int) = addValidRoom(addValidRoom(addValidRoom(addValidRoom(List(), row, left(col)), row, right(col)), up(row), col), down(row), col)  
  
  def addValidRoom(rooms:List[(Int,Int)], row:Int, col:Int):List[(Int,Int)] = {
	  if (isValidRoom(row, col))
	    (row, col) :: rooms
	  else
	    rooms
  }
  
  def isValidRoom(row:Int, col:Int) = {
	  peopleInTheRoom(row, col).find(_.isBad).map{p=>false}.getOrElse(true)
  }
  
  def left(col:Int) = if (col == 0) roomColumns - 1 else col - 1
  def right(col:Int) = if (col == (roomColumns - 1)) 0 else col + 1
  def up(row:Int) = if (row == 0) roomRows - 1 else row - 1
  def down(row:Int) = if (row == (roomRows - 1)) 0 else row + 1
  
  class Person (val id: Int) {
    var infected = false
    var sick = false
    var immune = false
    var dead = false
    var vip = false
    
    // demonstrates random number generation
    var row: Int = randomBelow(roomRows)
    var col: Int = randomBelow(roomColumns)
    
    addNextAction
    
    def myMobility = 
      if (mobilityAct){
        if (sick)
          mobility * mobilityActSickPersonRatio
        else
          mobility * mobilityActHealthPersonRatio
      }
      else
        mobility
        
    def isBad = sick || dead
    def isInfected = infected || immune
    
    def addNextAction {
	    afterDelay(randomBelow(myMobility) + 1){
	      if (!dead){
	    	findNextRoom(row, col) match {
	    	  case Some((r,c)) => moveToRoom(r,c) 
	    	  case None => addNextAction
	    	}
	      }
	    }
    }
    
    def moveToRoom(r:Int, c:Int){
	    row = r
	    col = c
	    if (!vip && !infected && isInfectedRoom(row, col)) {
	    	if (randomBelow(100) < transmisibilityRate)
	    	  becomeInfected()
	    }
    
	    addNextAction
    }
    
    def becomeInfected(){
	  infected = true
	  afterDelay(inf2SickPeriod) {
		  sick = true
		  afterDelay(inf2DiePeriod - inf2SickPeriod){
		    if (randomBelow(100) < deathRate){
		      dead = true	    		      
		    } 
		    else {
		    	afterDelay(inf2ImmunePeriod - inf2DiePeriod){
		    	  immune = true
		    	  sick = false

		    	  afterDelay(inf2HealtyPeriod - inf2ImmunePeriod){
		    		  infected = false
		    		  immune = false
		    	  }
		    	}
		    }
		  }
	  }
    }
  }
}
