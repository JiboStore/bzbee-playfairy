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

case class Books(var username: String, var myRoles: Array[String]) extends Subject {
  override def roles: List[SecurityRole] =
    List(SecurityRole("foo"),
         SecurityRole("bar"))

  override def permissions: List[UserPermission] =
    List(UserPermission("printers.edit"))

  override def identifier: String = username
  
}

object Books {
  import play.api.libs.json.Json
  
  // Generates Writes and Reads
  implicit val booksFormats = Json.format[Books]
//  implicit val booksFormat = Macros.handler[Books]
}

trait BooksRepo {
  
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

class BooksRepoImpl @Inject() (reactiveMongoApi: ReactiveMongoApi) extends BooksRepo {
  
  import com.playfairy.models.Books._
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
 
  def collection: JSONCollection = reactiveMongoApi.db.collection[JSONCollection]("books");
 
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
  
  /** Mine - using the BooksFormats */
  def findByName(name:String) : Future[List[Books]] = {
    val cursor: Cursor[Books] = collection.find(Json.obj("username" -> name)).cursor[Books]
    val futureBooksList: Future[List[Books]] = cursor.collect[List]()
    return futureBooksList
  }
  
  /** http://reactivemongo.org/releases/0.11/documentation/tutorial/write-documents.html */
  def updateByName(name: String, roles: List[String]) : Future[WriteResult] = {
    val selector = BSONDocument("username" -> name)
    val modifier = BSONDocument(
        "$set" -> BSONDocument(
            "username" -> name,
            "myRoles" -> roles
//         ),
//         "$unset" -> BSONDocument(
//             "_id" -> 1
//         )
            )
    )
    collection.update(selector, modifier)
  }
  
  def createByName(name:String) : Future[WriteResult] = {
    val u = new Books(name, Array("hello", "world"))
    val futureInsert = collection.insert(u);
    return futureInsert
  }
  
  def cleanDatabase() : Future[Boolean] = {
    collection.drop(true)
  }
 
}