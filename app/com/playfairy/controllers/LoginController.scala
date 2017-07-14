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
import scala.concurrent.duration._
import play.api.cache._
import play.api.data._
import play.api.data.Forms._
import com.playfairy.utils.PlayfairyUtils

class LoginController @Inject() (reactiveMongoApi: ReactiveMongoApi) (cache: CacheApi)
  extends Controller with MongoController with ReactiveMongoComponents {
  
  def reactiveMongoApi() : ReactiveMongoApi = {
    return reactiveMongoApi;
  }
  
  def personRepo = new PersonRepoImpl(reactiveMongoApi)
  
  def index() : Action[AnyContent] = Action.async { implicit request =>
    Future{
      val oSid = request.session.get("sessionId")
      // http://www.nurkiewicz.com/2014/06/optionfold-considered-unreadable.html
//      val sId: String = oSid.fold("")(_.toString)
//      val s: String = oSid.fold("")(sid => sid)
//      val sId: String = oSid.map( sid => sid ).getOrElse("")
//      val sId = oSid match {
//        case Some(sid) => {
//          sid
//        }
//        case None => {
//          ""
//        }
//      }
      val sId = oSid.map( sid => sid ).getOrElse("")
      Logger.debug("LoginController.index sessionId: " + sId)
      val oP = cache.get[Person](sId)
      Ok(com.playfairy.controllers.views.html.login.index(oP))
//      oP match {
//        case Some(person) => {
//          Ok(com.playfairy.controllers.views.html.login.index(person))
//        }
//        case None => {
//          Ok(com.playfairy.controllers.views.html.login.index())
//        }
//      }
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
            Redirect(com.playfairy.controllers.routes.LoginController.index()).withNewSession
          }
        },
        success => {
          val auth: Future[Option[Person]] = personRepo.authenticatePerson(success.username, success.password)
          auth.map({
                case Some(p) => {
                  val sessionId: String = PlayfairyUtils.generateSessionId()
                  Logger.debug("sessionId: " + sessionId)
                  cache.set(sessionId, p, 2.hours)
                  Ok(com.playfairy.controllers.views.html.login.index(Some(p))).withSession(
                      request.session + ("sessionId" -> sessionId)
                  )
                }
                case None => {
                  Redirect(com.playfairy.controllers.routes.LoginController.index())
                    .withNewSession
                    .flashing("failed" -> "unable to login")
                }
          })
        }
    )
  }
  
  def logout(): Action[AnyContent] = Action.async { implicit request =>
    Future {
      val oSid = request.session.get("sessionId")
      val sId = oSid.map( sid => {
        cache.remove(sid)
        sid
      }).getOrElse("")
      Logger.debug("LoginController.index sessionId: " + sId)
      Redirect(com.playfairy.controllers.routes.LoginController.index()).withNewSession
    }
  }
  
}