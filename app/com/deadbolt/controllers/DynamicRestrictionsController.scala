package com.deadbolt.controllers

import javax.inject.Inject

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions}
import play.api.mvc.Controller
import com.deadbolt.security.HandlerKeys
import com.deadbolt.views.html.accessOk

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  *
  * @author Steve Chaloner (steve@objectify.be)
  */
class DynamicRestrictionsController @Inject()(deadbolt: DeadboltActions, handlers: HandlerCache, actionBuilder: ActionBuilders) extends Controller {
  def pureLuck = deadbolt.Dynamic(name = "pureLuck")() { authRequest =>
    Future {
             Ok(accessOk())
           }
                                                       }

  def noWayJose = deadbolt.Dynamic(name = "pureLuck", handler = handlers(HandlerKeys.altHandler))() { authRequest =>
    Future {
             Ok(accessOk())
           }
                                                                                                    }

  def pureLuck_fromBuilder = actionBuilder.DynamicAction(name = "pureLuck").defaultHandler() { authRequest =>
    Future {
             Ok(accessOk())
           }
                                                                                             }

  def noWayJose_fromBuilder = actionBuilder.DynamicAction(name = "pureLuck").key(HandlerKeys.altHandler) { authRequest =>
    Future {
             Ok(accessOk())
           }
                                                                                                         }
}
