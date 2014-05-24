package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._

object HomeController extends Controller {

    def index = Action {
        Ok(views.html.tasks())
    }
}
