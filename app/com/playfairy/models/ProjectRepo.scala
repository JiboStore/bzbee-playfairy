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
import java.util.Date

case class Project(
    var name: String, 
    var versions: Array[Version]
) {
  
}

case class Version(
    var name: String, 
    var createdDate: Date
) {
  
}

object Version {
  implicit var versionFormat = Json.format[Version]
}

object Project {
  implicit var projectFormat = Json.format[Project]
}

//case class Chapters(var num: Int, var chapterUrl: String) {
//  
//}
//
//object Chapters {
//  import play.api.libs.json.Json
//  
//  implicit val chaptersFormat = Json.format[Chapters]
//}
//
//object Project {
//  import play.api.libs.json.Json
//  
//  // Generates Writes and Reads
//  implicit val projectFormats = Json.format[Project]
////  implicit val projectFormat = Macros.handler[Project]
//}

trait ProjectRepo {
  
}

class ProjectRepoImpl @Inject() (reactiveMongoApi: ReactiveMongoApi) extends ProjectRepo {
  
  import com.playfairy.models.Project._
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
 
  def collection: JSONCollection = reactiveMongoApi.db.collection[JSONCollection]("project");
  def bsonCollection: BSONCollection = reactiveMongoApi.db.collection[BSONCollection]("project");
   
  /** Mine - using the ProjectFormats */
//  def findByName(name:String) : Future[List[Project]] = {
//    val cursor: Cursor[Project] = collection.find(Json.obj("username" -> name)).cursor[Project]
//    val futureProjectList: Future[List[Project]] = cursor.collect[List]()
//    return futureProjectList
//  }
  
  /** http://reactivemongo.org/releases/0.11/documentation/tutorial/write-documents.html */
//  def updateByName(name: String, roles: List[String]) : Future[WriteResult] = {
//    val selector = BSONDocument("username" -> name)
//    val modifier = BSONDocument(
//        "$set" -> BSONDocument(
//            "username" -> name,
//            "myRoles" -> roles
//            )
//    )
//    collection.update(selector, modifier)
//  }
  
//  def createByName(name:String) : Future[WriteResult] = {
//    val ch = Array(Chapters(1, "http://www.mangareader.net/video-girl-ai/1"),
//        Chapters(2, "http://www.mangareader.net/video-girl-ai/2"),
//        Chapters(3, "http://www.mangareader.net/video-girl-ai/3")
//    )
//    val u = new Project(name, Array("hello", "world"), ch)
//    val futureInsert = collection.insert(u);
//    return futureInsert
//  }
//  
//  def countAllRecords() : Future[Int] = {
//    val query = BSONDocument("_id" -> BSONDocument("$exists" -> true))
//    val command = Count(query)
//    val result: Future[CountResult] = bsonCollection.runCommand(command)
//    result.map( res => {
//      val count: Int = res.value
//      count
//    });
//  }
//  
//  def cleanDatabase() : Future[Boolean] = {
//    collection.drop(true)
//  }
 
}