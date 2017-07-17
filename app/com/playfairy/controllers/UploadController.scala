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

class UploadController @Inject() (reactiveMongoApi: ReactiveMongoApi) (cache: CacheApi)
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
      Ok(com.playfairy.controllers.views.html.upload.index(oP))
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
  
  // https://github.com/playframework/play-scala-fileupload-example/tree/2.5.x
  def ipa(): Action[AnyContent] = Action.async { implicit request =>
    val szHostname = request.host
    Future {
      Ok("not implemented")
    }
  }
  
}