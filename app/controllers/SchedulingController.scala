package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import play.api.db.slick.DB
import play.api.Play.current
import com.github.nscala_time.time.Imports._

import models._

object SchedulingController extends Controller {
  def create = Action { implicit request =>
 //   val existingTasks = DB.withSession { implicit session =>
//      TableQuery[TaskModel].list
//    }

    Ok(
   //   Json.toJson( existingTasks )
   "lol"
    ).as("text/text")
  }
}
