package com.playfairy.controllers

import play.api.mvc._
import javax.inject.Inject
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.ReactiveMongoComponents
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import reactivemongo.bson._
import reactivemongo.api.commands.WriteResult
import com.playfairy.models._
import play.Logger

//import javax.inject.Inject
//import play.api.libs.concurrent.Execution.Implicits.defaultContext
//import play.api.libs.json.Json
//import play.api.mvc._
//import play.modules.reactivemongo.MongoController
//import play.modules.reactivemongo.ReactiveMongoApi
//import play.modules.reactivemongo.ReactiveMongoComponents
//import reactivemongo.bson.BSONDocument
//import reactivemongo.bson.BSONObjectID
//import repos.WidgetRepoImpl

class BooksTestController @Inject() (reactiveMongoApi: ReactiveMongoApi) 
  extends Controller with MongoController with ReactiveMongoComponents {
  
  import com.playfairy.controllers.WidgetFields._
  
  def reactiveMongoApi() : ReactiveMongoApi = {
    return reactiveMongoApi;
  }
  
  def booksRepo = new BooksRepoImpl(reactiveMongoApi)
  
  def findByName(name: String) = Action.async {
    Logger.debug("findByName: " + name);
    var future = booksRepo.findByName(name)
    future.map( listBooks => {
//      listBooks.map( u => Ok( Json.toJson(u) ) )
      Ok( Json.toJson(listBooks) )
    });
  }
  
  def updateByName(name: String) = Action.async {
    var newRole = List("one", "two", "three")
    var future = booksRepo.updateByName("hello", newRole)
    future.map(writeResult => {
      if ( writeResult.ok ) {
        Ok("ok")
      } else {
        Ok("problem")
      }
    });
  }
  
  def seederPopulate = Action.async {
    val future = booksRepo.createByName("hello")
    future.map(writeResult => {
      if ( writeResult.ok ) {
//        Redirect("http://www.apple.com/sg");
        Ok("ok");
      } else {
//        Redirect("http://www.microsoft.com/");
        Ok("problem")
      }
    });
  }
  
  def seederClean = Action.async {
    val future = booksRepo.cleanDatabase();
    future.map(result => {
      if ( result ) {
//        Redirect("http://www.apple.com/sg");
        Ok("ok")
      } else {
//        Redirect("http://www.microsoft.com/");
        Ok("problem")
      }
    });
  }
  
}