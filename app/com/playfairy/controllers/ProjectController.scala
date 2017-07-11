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

class ProjectController @Inject() (reactiveMongoApi: ReactiveMongoApi) 
  extends Controller with MongoController with ReactiveMongoComponents {
  
  def reactiveMongoApi() : ReactiveMongoApi = {
    return reactiveMongoApi;
  }
  
//  def booksRepo = new BooksRepoImpl(reactiveMongoApi)
//  def personRepo = new PersonRepoImpl(reactiveMongoApi)
  def projectRepo = new ProjectRepoImpl(reactiveMongoApi)
  
  def index() : Action[AnyContent] = Action.async {
    Future{
      Ok(com.playfairy.controllers.views.html.project.index())
    }
  }
  
  case class CreateProjectFormData(projectName: String)
  
  def create() : Action[AnyContent] = Action.async { implicit request =>
    val formData = Form(
        mapping(
            "projectName" -> nonEmptyText
        )(CreateProjectFormData.apply)(CreateProjectFormData.unapply)
    )
    formData.bindFromRequest.fold(
        hasErrors => {
          Future {
            BadRequest("form binding error")
          }
        },
        success => {
          val futureResult = projectRepo.createProject(success.projectName)
          futureResult.map( writeResult => {
            if ( writeResult.ok ) {
              Ok("project created")
            } else {
              Ok("unable to create project")
            }
          })
        }
    )
  }
  
  def get_addversion(): Action[AnyContent] = Action.async {
    Future {
      Ok(com.playfairy.controllers.views.html.project.addversionform())
    }
  }
  
  case class CreateVersionFormData(projectName: String, versionName: String)
  
  def post_addversion(): Action[AnyContent] = Action.async { implicit request =>
    val formData = Form(
        mapping(
            "projectName" -> nonEmptyText,
            "versionName" -> nonEmptyText
        )(CreateVersionFormData.apply)(CreateVersionFormData.unapply)
    )
    formData.bindFromRequest().fold(
        hasErrors => {
          Future {
            BadRequest("form binding error")
          }
        }, 
        success => {
          val addResult: Future[Boolean] = projectRepo.addVersion(success.projectName, success.versionName)
          addResult.map({
              case true => Ok("version added")
              case false => Ok("unable to add version")
          })
        }
    )
  }
  
//  case class SignupFormData(username: String, password:String)
//  
//  def createPerson() : Action[AnyContent] = Action.async { implicit request =>
//    val signupForm = Form(
//        mapping(
//            "username" -> nonEmptyText,
//            "password" -> nonEmptyText
//        )(SignupFormData.apply)(SignupFormData.unapply)
//    )
//    signupForm.bindFromRequest.fold(
//        hasErrors => {
//          Future {
//            BadRequest("form binding error")
//          }
//        },
//        success => {
//          val res = personRepo.createPerson( success.username, success.password )
//          res.map( r => {
//            if ( r.ok ) {
//              Ok("inserted")
//            } else {
//              Ok("failed to insert")
//            }
//          })
//        }
//    )
//  }
  
//  def findByName(name: String) = Action.async {
//    Logger.debug("findByName: " + name);
//    var future = booksRepo.findByName(name)
//    future.map( listBooks => {
//      Ok( Json.toJson(listBooks) )
//    });
//  }
  
//  def updateByName(name: String) = Action.async {
//    var newRole = List("one", "two", "three")
//    var future = booksRepo.updateByName("hello", newRole)
//    future.map(writeResult => {
//      if ( writeResult.ok ) {
//        Ok("ok")
//      } else {
//        Ok("problem")
//      }
//    });
//  }
  
}