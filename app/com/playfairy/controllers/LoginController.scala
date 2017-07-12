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

class LoginController @Inject() (reactiveMongoApi: ReactiveMongoApi) 
  extends Controller with MongoController with ReactiveMongoComponents {
  
  def reactiveMongoApi() : ReactiveMongoApi = {
    return reactiveMongoApi;
  }
  
  def personRepo = new PersonRepoImpl(reactiveMongoApi)
  
  def index() : Action[AnyContent] = Action.async {
    Future{
      Ok(com.playfairy.controllers.views.html.login.index())
    }
  }
  
  case class LoginFormData(username: String, password:String)
  
  def create() : Action[AnyContent] = Action.async { implicit request =>
    val signupForm = Form(
        mapping(
            "username" -> nonEmptyText,
            "password" -> nonEmptyText
        )(LoginFormData.apply)(LoginFormData.unapply)
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
  
  def login() : Action[AnyContent] = Action.async { implicit request =>
    val loginForm = Form(
        mapping(
            "username" -> nonEmptyText,
            "password" -> nonEmptyText
        )(LoginFormData.apply)(LoginFormData.unapply)
    )
    loginForm.bindFromRequest.fold(
        hasErrors => {
          Future {
            Redirect(com.playfairy.controllers.routes.LoginController.index())
          }
        },
        success => {
          val auth: Future[Boolean] = personRepo.authenticatePerson(success.username, success.password)
          auth.map({
                case true => Ok("success")
                case false => Ok("unauthorized")
          })
        }
    )
  }
  
  def logout() = TODO
  
}