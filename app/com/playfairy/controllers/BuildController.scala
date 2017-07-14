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
import com.playfairy.utils.PlayfairyUtils
import play.api.cache.CacheApi

class BuildController @Inject() (reactiveMongoApi: ReactiveMongoApi) (cache: CacheApi)
  extends Controller with MongoController with ReactiveMongoComponents {
  
  def reactiveMongoApi() : ReactiveMongoApi = {
    return reactiveMongoApi;
  }
  
  def buildRepo = new BuildRepoImpl(reactiveMongoApi)
  
  def index(): Action[AnyContent] = Action.async { implicit request =>
//    implicit val sess = request.session
    implicit val cach = cache
    Future{
      var oP = PlayfairyUtils.getPersonFromCache
      Ok(com.playfairy.controllers.views.html.build.index(oP))
    }
  }
  
  case class CreateBuildFormData(svnRev: String, gitRev: String, versionName: String, projectName: String)
  
  def create(): Action[AnyContent] = Action.async { implicit request =>
    val formData = Form(
        mapping(
            "svnRev" -> nonEmptyText,
            "gitRev" -> nonEmptyText,
            "versionName" -> nonEmptyText,
            "projectName" -> nonEmptyText
        )(CreateBuildFormData.apply)(CreateBuildFormData.unapply)
    )
    val result = formData.bindFromRequest.fold(
      hasErrors => {
        Future {
          BadRequest("form binding error")
        }
      }, 
      success => {
        val futureResult = buildRepo.createBuild(success.svnRev, success.gitRev, success.projectName, success.versionName)
        futureResult.map( writeResult => {
          if ( writeResult.ok ) {
            Ok("build saved")
          } else {
            Ok("unable to save build")
          }
        }).recover ({
          case e: Exception => Ok("Failed with exception: " + e)
        })
    })
    result
  }
  
//  def index() : Action[AnyContent] = Action.async {
//    Future{
//      Ok(com.playfairy.controllers.views.html.project.index())
//    }
//  }
//  
//  case class CreateProjectFormData(projectName: String)
//  
//  def create() : Action[AnyContent] = Action.async { implicit request =>
//    val formData = Form(
//        mapping(
//            "projectName" -> nonEmptyText
//        )(CreateProjectFormData.apply)(CreateProjectFormData.unapply)
//    )
//    formData.bindFromRequest.fold(
//        hasErrors => {
//          Future {
//            BadRequest("form binding error")
//          }
//        },
//        success => {
//          val futureResult = projectRepo.createProject(success.projectName)
//          futureResult.map( writeResult => {
//            if ( writeResult.ok ) {
//              Ok("project created")
//            } else {
//              Ok("unable to create project")
//            }
//          })
//        }
//    )
//  }
//  
//  def get_addversion(): Action[AnyContent] = Action.async {
//    Future {
//      Ok(com.playfairy.controllers.views.html.project.addversionform())
//    }
//  }
//  
//  case class CreateVersionFormData(projectName: String, versionName: String)
//  
//  def post_addversion(): Action[AnyContent] = Action.async { implicit request =>
//    val formData = Form(
//        mapping(
//            "projectName" -> nonEmptyText,
//            "versionName" -> nonEmptyText
//        )(CreateVersionFormData.apply)(CreateVersionFormData.unapply)
//    )
//    formData.bindFromRequest().fold(
//        hasErrors => {
//          Future {
//            BadRequest("form binding error")
//          }
//        }, 
//        success => {
//          val addResult: Future[Boolean] = projectRepo.addVersion(success.projectName, success.versionName)
//          addResult.map({
//              case true => Ok("version added")
//              case false => Ok("unable to add version")
//          })
//        }
//    )
//  }
  
}