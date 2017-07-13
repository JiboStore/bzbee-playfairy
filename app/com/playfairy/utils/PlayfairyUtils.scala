package com.playfairy.utils

import java.security.SecureRandom
import java.math.BigInteger
import play.api.mvc.Session
import play.api.cache.CacheApi
import com.playfairy.models.Person
import scala.concurrent.duration._

object PlayfairyUtils {
  
  val secureRandom: SecureRandom = new SecureRandom()
  
  def generateSessionId() : String = {
    // https://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string
    new BigInteger(130, secureRandom).toString(32)
  }
  
  def createPassword(clearString: String) : String = {
   "unused"
  }
  
  def getPersonFromCache(implicit session: Session, cache: CacheApi): Option[Person] = {
    val optionSid = session.get("sessionId")
    val sId = optionSid.map({ s => s }).getOrElse("")
    cache.get[Person](sId)
  }
  
  def setPersonToCache(sessionId: String, person: Person)(implicit cache: CacheApi) = {
    cache.set(sessionId, person, 2.hours)
  }
  
  // mapping an Option: http://www.nurkiewicz.com/2014/06/optionfold-considered-unreadable.html

}
