package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._

object TestController extends Controller {
    // We will parse our form data into this object
    case class WhoIsYourDaddy(daddyName: String)

    // This is the mapping between the form data and our object
    val userForm = Form(mapping(
        "daddyName" -> text
    )(WhoIsYourDaddy.apply)(WhoIsYourDaddy.unapply))
    
    def index = Action {
        Ok(views.html.yourDaddy(ExampleModel.whoIsYourDaddy))
    }

    def update = Action { implicit request =>
        // parse the form data
        val daddy = userForm.bindFromRequest.get
        
        // update your daddy
        ExampleModel.whoIsYourDaddy = daddy.daddyName
        
        // redirect to home page
        Redirect("/")
    }

}