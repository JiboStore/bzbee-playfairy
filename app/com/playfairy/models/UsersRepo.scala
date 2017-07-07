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

case class Users(var username: String, var myRoles: Array[String]) extends Subject {
  override def roles: List[SecurityRole] =
    List(SecurityRole("foo"),
         SecurityRole("bar"))

  override def permissions: List[UserPermission] =
    List(UserPermission("printers.edit"))

  override def identifier: String = username
  
}

object Users {
  import play.api.libs.json.Json
  
  // Generates Writes and Reads
  implicit val usersFormats = Json.format[Users]
//  implicit val usersFormat = Macros.handler[Users]
}

trait UsersRepo {
  
//  def find() (implicit ec: ExecutionContext) : Future[List[JsObject]]
//  
//  def select(selector: BSONDocument) (implicit ec: ExecutionContext) : Future[Option[JsObject]]
//  
//  def update(selector: BSONDocument, update: BSONDocument) (implicit ec: ExecutionContext) : Future[WriteResult]
//  
//  def remove(document: BSONDocument) (implicit ec: ExecutionContext) : Future[WriteResult]
//  
//  def save(document: BSONDocument) (implicit ec: ExecutionContext) : Future[WriteResult]
  
}

class UsersRepoImpl @Inject() (reactiveMongoApi: ReactiveMongoApi) extends UsersRepo {
  
  import com.playfairy.models.Users._
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
 
  def collection: JSONCollection = reactiveMongoApi.db.collection[JSONCollection]("users");
 
//  override def find()(implicit ec: ExecutionContext): Future[List[JsObject]] = {
//    val genericQueryBuilder = collection.find(Json.obj());
//    val cursor = genericQueryBuilder.cursor[JsObject](ReadPreference.Primary);
//    cursor.collect[List]()
//  }
// 
//  override def select(selector: BSONDocument)(implicit ec: ExecutionContext): Future[Option[JsObject]] = {
//    collection.find(selector).one[JsObject]
//  }
// 
//  override def update(selector: BSONDocument, update: BSONDocument)(implicit ec: ExecutionContext): Future[WriteResult] = {
//    collection.update(selector, update)
//  }
// 
//  override def remove(document: BSONDocument)(implicit ec: ExecutionContext): Future[WriteResult] = {
//    collection.remove(document)
//  }
// 
//  override def save(document: BSONDocument)(implicit ec: ExecutionContext): Future[WriteResult] = {
//    collection.update(BSONDocument("_id" -> document.get("_id").getOrElse(BSONObjectID.generate)), document, upsert = true)
//  }
  
  /** Mine - using the UsersFormats */
  def findByName(name:String) : Future[List[Users]] = {
    val cursor: Cursor[Users] = collection.find(Json.obj("username" -> name)).cursor[Users]
    val futureUsersList: Future[List[Users]] = cursor.collect[List]()
    return futureUsersList
  }
  
  def createByName(name:String) : Future[WriteResult] = {
    val u = new Users(name, Array("hello", "world"))
    val futureInsert = collection.insert(u);
    return futureInsert
  }
  
  def cleanDatabase() : Future[Boolean] = {
    collection.drop(true)
  }
 
}