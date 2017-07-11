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
import reactivemongo.api.collections.bson.BSONQueryBuilder
import reactivemongo.api.collections.GenericQueryBuilder
import scala.collection.mutable.ListBuffer

case class Project(
    var name: String, 
    var packageName: String,
    var versions: List[Version]
) {
  
}

case class Version(
    var name: String, 
    var createdDate: Date
) {
  
}

object Version {
  implicit var versionJsonFormat = Json.format[Version]
  implicit var versionBsonFormat = Macros.handler[Version]
}

object Project {
  implicit var projectJsonFormat = Json.format[Project]
  implicit var projectBsonFormat = Macros.handler[Project]
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
  def futureCollection: Future[BSONCollection] = reactiveMongoApi.database.map( db => {
    db.collection[BSONCollection]("project")
  });
  
  def createProject(name: String, packageName: String): Future[WriteResult] = {
    val versions = List(Version("1.0", new Date))
    val project = Project(name, packageName, versions)
    futureCollection.flatMap( db => {
      db.insert(project)
    })
  }
  
  def addVersion(projectName: String, versionName: String): Future[Boolean] = {
    val futureProject = findProjectByName(projectName)
    val result: Future[Boolean] = futureProject.flatMap({
      case Some(p: Project) => {
        val version = Version(versionName, new Date())
        val versions = new ListBuffer[Version]
        versions ++= p.versions
        versions += version
        p.versions = versions.toList
        val fUpdate = updateProjectByName(projectName, p)
        val fRes = fUpdate.map( res => {
          if ( res.ok )
            true
          else
            false
        })
        fRes
      }
      case None => {
        Future {
          false
        }
      }
    })
    return result
  }
  
  def findProjectByName(name: String): Future[Option[Project]] = {
    val futureProject: Future[Option[Project]] = futureCollection.flatMap( db => {
      val queryParams: BSONDocument = BSONDocument("name" -> name)
      db.find(queryParams).one[Project]
    })
    return futureProject
  }
  
  def updateProjectByName(name: String, project: Project): Future[WriteResult] = {
    val selector = BSONDocument("name" -> name)
    val modifier = BSONDocument(
        "$set" -> BSONDocument(
            "name" -> project.name,
            "versions" -> project.versions
         )
    )
    val result = futureCollection.flatMap( db => {
      db.update(selector, modifier)
    })
    return result
  }
   
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