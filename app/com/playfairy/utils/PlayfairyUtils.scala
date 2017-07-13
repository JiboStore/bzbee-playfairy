package com.playfairy.utils

import java.security.SecureRandom
import java.math.BigInteger

object PlayfairyUtils {
  
  val secureRandom: SecureRandom = new SecureRandom()
  
  def generateSessionId() : String = {
    // https://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string
    new BigInteger(130, secureRandom).toString(32)
  }
  
  def createPassword(clearString: String) : String = {
   "unused"
  }
  
  // mapping an Option: http://www.nurkiewicz.com/2014/06/optionfold-considered-unreadable.html

}
