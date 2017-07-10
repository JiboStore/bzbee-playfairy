package com.playfairy.models

import scala.concurrent._

import javax.inject.Inject
import play.api.libs.json._
//import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection._
import reactivemongo.api.ReadPreference
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson._
import be.objectify.deadbolt.scala.models.Subject
import com.deadbolt.models.UserPermission
import com.deadbolt.models.SecurityRole
import reactivemongo.api.Cursor
import scala.collection.immutable.HashMap
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.bson.BSONCountCommand.{ Count, CountResult }
import reactivemongo.api.commands.bson.BSONCountCommandImplicits._
import reactivemongo.bson.BSONDocument
import org.mindrot.jbcrypt.BCrypt

case class Person(
    var username: String, 
    var pwhash: String, 
    var pwsalt: String
//    var pwsalt: String,
//    var role: List[String],
//    var projectIds: List[BSONObjectID]
) extends Subject {
  
  override def identifier: String = {
    username + pwhash
  }
  
  override def roles: List[SecurityRole] = {
    List(SecurityRole("none"))
//    role.map( s => {
//      SecurityRole(s)
//    })
  }
  
  override def permissions: List[UserPermission] = {
    List(UserPermission("none"))
//    role.map( s => {
//      UserPermission(s)
//    })
  }
  
  var projects: Array[Project] = null
  
}

object Person {
  import play.api.libs.json.Json
  
  // Generates Writes and Reads
  implicit val personJsonFormats = Json.format[Person]
  implicit val personBsonFormats = Macros.handler[Person]
//  implicit val versionFormats = Json.format[Version]
//  implicit val projectFormats = Json.format[Project]
  
//  implicit val personFormat = Macros.handler[Person]
}

trait PersonRepo {
  
}

class PersonRepoImpl @Inject() (reactiveMongoApi: ReactiveMongoApi) extends PersonRepo {
  
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
 
  def jsonCollection: JSONCollection = reactiveMongoApi.db.collection[JSONCollection]("person");
  def bsonCollection: BSONCollection = reactiveMongoApi.db.collection[BSONCollection]("person");
  
  def createPerson(name: String, password: String) : Future[WriteResult] = {
    var salt = BCrypt.gensalt()
    var pw = BCrypt.hashpw(password, salt)
//    val u = new Person(name, pw, salt, List(), List())
    val u = new Person(name, pw, salt)
    bsonCollection.insert(u)
  }
  
  def authenticatePerson(name: String, password: String) : Future[Boolean] = {
    val query: BSONDocument = BSONDocument("username" -> name)
    val person: Future[Option[Person]] = bsonCollection.find(query).one[Person]
    // simplified
    person.map{
      case Some(p) => {
        BCrypt.checkpw(password, p.pwsalt)
      }
      case None => {
        false
      }
    }
    // the real meaning
//    person.map( op => {
//      op match {
//        case Some(p) => {
//          BCrypt.checkpw(password, p.pwsalt)
//        }
//        case None => {
//          false
//        }
//      }
//    })
  }
  
  def countAllRecords() : Future[Int] = {
    val query = BSONDocument("_id" -> BSONDocument("$exists" -> true))
    val command = Count(query)
    val result: Future[CountResult] = bsonCollection.runCommand(command)
    result.map( res => {
      val count: Int = res.value
      count
    });
  }
 
}