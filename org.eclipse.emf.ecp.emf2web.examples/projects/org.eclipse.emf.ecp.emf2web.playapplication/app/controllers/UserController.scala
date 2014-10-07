package controllers

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Action
import play.modules.reactivemongo.MongoController
import org.qbproject.api.schema.QBSchema._
import org.qbproject.api.controllers.{JsonHeaders, QBCrudController}
import org.qbproject.api.mongo.{QBCollectionValidation, QBMongoCollection}
import org.qbproject.api.routing.QBRouter
import play.api.libs.json.{JsUndefined, JsValue, Json}
object UserController extends MongoController with QBCrudController {

  lazy val collection = new QBMongoCollection("user")(db) with QBCollectionValidation {
    override def schema = UserSchema.modelSchema
  }

  override def createSchema = UserSchema.modelSchema -- "id"

  def getView = JsonHeaders {
    Action {
      Ok(Json.toJson(UserSchema.viewSchema))
    }
  }
  
  def getModel = JsonHeaders {
    Action {
      Ok(Json.toJson(UserSchema.modelSchema))
    }
  }
}

object UserRouter extends QBRouter {
  override def qbRoutes = UserController.crudRoutes
}
