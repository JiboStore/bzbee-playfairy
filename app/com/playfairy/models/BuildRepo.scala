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

case class Build(
    var svnRev: String, 
    var gitRev: String, 
    var uploadedDate: Date,
    var versionName: String,
    var projectName: String
) {
  
}

object Build {
  
  // Generates Writes and Reads
  implicit val buildJsonFormats = Json.format[Build]
  implicit val buildBsonFormats = Macros.handler[Build]
//  implicit val buildFormat = Macros.handler[Build]
}

trait BuildRepo {
  
}

class BuildRepoImpl @Inject() (reactiveMongoApi: ReactiveMongoApi) extends BuildRepo {
  
  import com.playfairy.models.Build._
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
 
  def collection: JSONCollection = reactiveMongoApi.db.collection[JSONCollection]("build");
  def bsonCollection: BSONCollection = reactiveMongoApi.db.collection[BSONCollection]("build");
  def futureCollection: Future[BSONCollection] = reactiveMongoApi.database.map( db => {
    db.collection[BSONCollection]("build")
  });
  
  def projectRepo = new ProjectRepoImpl(reactiveMongoApi)
  
  def createBuild(svnRev: String, gitRev: String, projectName: String, versionName: String): Future[WriteResult] = {
    val futureProject: Future[Option[Project]] = projectRepo.findProjectByName(projectName)
    val futureBuild: Future[Build] = futureProject.map({
      case Some(p: Project) => {
       // project found, create the Build obj
        Build(svnRev, gitRev, new Date(), versionName, p.name)
      }
      case None => {
        // project not found
        throw new Exception("project not found: " + projectName);
      }
    })
    
    var res = for (
        db <- futureCollection;
        build <- futureBuild;
        writeResult <- db.insert(build)
    ) yield {
      writeResult
    }
    // equivalent
//    var res = futureCollection.flatMap( db => {
//      futureBuild.flatMap( build => {
//        db.insert(build)
//      })
//    })
    
    return res
  }
  
  /** Mine - using the BuildFormats */
//  def findByName(name:String) : Future[List[Build]] = {
//    val cursor: Cursor[Build] = collection.find(Json.obj("username" -> name)).cursor[Build]
//    val futureBuildList: Future[List[Build]] = cursor.collect[List]()
//    return futureBuildList
//  }
  
  /** http://reactivemongo.org/releases/0.11/documentation/tutorial/write-documents.html */
//  def updateByName(name: String, roles: List[String]) : Future[WriteResult] = {
//    val selector = BSONDocument("username" -> name)
//    val modifier = BSONDocument(
//        "$set" -> BSONDocument(
//            "username" -> name,
//            "myRoles" -> roles
//        )
//    )
//    collection.update(selector, modifier)
//  }
 
}