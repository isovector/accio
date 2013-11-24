package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._

object TestController extends Controller {
    case class WhoIsYourDaddy(daddyName: String)

    val userForm = Form(mapping(
        "daddyName" -> text
    )(WhoIsYourDaddy.apply)(WhoIsYourDaddy.unapply))
    
    def index = Action {
        Ok(views.html.yourDaddy(ExampleModel.whoIsYourDaddy))
    }

    def update = Action { implicit request =>
        val daddy = userForm.bindFromRequest.get
        ExampleModel.whoIsYourDaddy = daddy.daddyName
        Redirect("/")
    }

}