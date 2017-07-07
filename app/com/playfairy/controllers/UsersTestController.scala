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

class UsersController @Inject() (reactiveMongoApi: ReactiveMongoApi) 
  extends Controller with MongoController with ReactiveMongoComponents {
  
  import com.playfairy.controllers.WidgetFields._
  
  def reactiveMongoApi() : ReactiveMongoApi = {
    return reactiveMongoApi;
  }
  
  def usersRepo = new UsersRepoImpl(reactiveMongoApi)
  
  def seederPopulate = Action.async {
    val future = usersRepo.createByName("hello")
    future.map(writeResult => {
      if ( writeResult.ok ) {
        Redirect("http://www.apple.com/sg");
      } else {
        Redirect("http://www.microsoft.com/");
      }
    });
  }
  
  def seederClean = Action.async {
    val future = usersRepo.cleanDatabase();
    future.map(result => {
      if ( result ) {
        Redirect("http://www.apple.com/sg");
      } else {
        Redirect("http://www.microsoft.com/");
      }
    });
  }
  
}