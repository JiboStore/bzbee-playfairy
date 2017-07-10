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
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success
import play.api.data._
import play.api.data.Forms._

class SignupController @Inject() (reactiveMongoApi: ReactiveMongoApi) 
  extends Controller with MongoController with ReactiveMongoComponents {
  
  import com.playfairy.controllers.WidgetFields._
  
  def reactiveMongoApi() : ReactiveMongoApi = {
    return reactiveMongoApi;
  }
  
  def booksRepo = new BooksRepoImpl(reactiveMongoApi)
  def personRepo = new PersonRepoImpl(reactiveMongoApi)
  
  def index() : Action[AnyContent] = Action.async {
    Future{
      Ok(com.playfairy.controllers.views.html.signup.index())
    }
  }
  
//  def create() : Action[AnyContent] = Action.async {
//    Future {
//      Ok("done")
//    }
//  }
  
  case class SignupFormData(username: String, password:String)
  
  def create() : Action[AnyContent] = Action.async { implicit request =>
    val signupForm = Form(
        mapping(
            "username" -> nonEmptyText,
            "password" -> nonEmptyText
        )(SignupFormData.apply)(SignupFormData.unapply)
    )
    signupForm.bindFromRequest.fold(
        hasErrors => {
          Future {
            BadRequest("form binding error")
          }
        },
        success => {
          val res = personRepo.createPerson( success.username, success.password )
          res.map( r => {
            if ( r.ok ) {
              Ok("inserted")
            } else {
              Ok("failed to insert")
            }
          })
        }
    )
  }
  
  def findByName(name: String) = Action.async {
    Logger.debug("findByName: " + name);
    var future = booksRepo.findByName(name)
    future.map( listBooks => {
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
    val count: Future[Int] = booksRepo.countAllRecords()
    val countResult: Future[Boolean] = count.map( c => {
      if ( c > 0 ) {
        true
      } else {
        false
      }
    });
    
    val createResult = count.map( c => {
      if ( c < 1 ) {
        val future = booksRepo.createByName("hello")
        future;
      } else {
        throw new Exception("already created")
      }
    });
    
    createResult.map(
       res => Ok("ok")
    ).recover{ 
      case t => Ok("error: " + t)
    }
    
  }
  
  def seederClean = Action.async {
    val count: Future[Int] = booksRepo.countAllRecords()
   
    val clean = count.flatMap( c => {
      if ( c > 0 ) {
        val future = booksRepo.cleanDatabase();
        future;
      } else {
        throw new Exception("nothing to clean")
      }
    });
    
    // this is also ok: https://stackoverflow.com/a/44977295/474330
    clean.map( b => b match {
      case true => Ok("success")
      case false => Ok("failed")
    }).recover {
      case t => Ok("error: " + t)
    }
    
  }
  
}