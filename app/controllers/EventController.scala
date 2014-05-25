package controllers

import play.api._
import play.api.libs.json._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.db.slick.DB
import play.api.Play.current
import com.github.nscala_time.time.Imports._

import models._

import scala.collection.mutable.HashMap
import play.api.db.slick.Config.driver.simple._

object EventController extends Controller {
    def list = Action {
        val existingTasks = DB.withSession { implicit session =>
            TableQuery[EventModel].list
        }
        Ok(
            Json.toJson( existingTasks )
        ).as("text/text")
    }

    def delete(id: Int) = Action {
        val drop = DB.withSession { implicit session =>
            TableQuery[EventModel].where(_.id === id).delete
        }
        Ok
    }

    def create = Action { implicit request =>
        val taskID = Form(
            "task" -> optional(number)
        ).bindFromRequest.get

        val when = Form(
            "when" -> text
        ).bindFromRequest.get

        val duration = Form(
            "duration" -> number
        ).bindFromRequest.get

        val where = Form(
            "where" -> optional(text)
        ).bindFromRequest.get

        val description = Form(
            "description" -> optional(text)
        ).bindFromRequest.get

        val task = taskID match {
            case Some(id) =>
               TaskController.findByID(id)
            case None =>
                None
        }

        val event = new Event(
            task = task,
            when = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parseDateTime(when),
            duration = new Duration(duration),
            where = where.getOrElse(""),
            description = description.getOrElse(""))

        DB.withSession { implicit session =>
            TableQuery[EventModel] += event
        }

        Ok
    }
}
