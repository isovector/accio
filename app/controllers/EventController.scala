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
        case class EventFormData(
            taskId: Option[Int],
            startTime: String,
            endTime: String,
            where: Option[String],
            description: Option[String],
            eventType: String
        )

        val formData = Form(mapping(
            "task" -> optional(number),
            "start_date" -> text,
            "end_date" -> text,
            "where" -> optional(text),
            "description" -> optional(text),
            "eventType" -> text
        )(EventFormData.apply)(EventFormData.unapply)).bindFromRequest.get

        val task = formData.taskId match {
            case Some(id) =>
               TaskController.findByID(id)
            case None =>
                None
        }

        val dateMatcher = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

        val event = new Event(
            task = task,
            startTime = dateMatcher.parseDateTime(formData.startTime),
            endTime = dateMatcher.parseDateTime(formData.endTime),
            where = formData.where.getOrElse(""),
            eventType = EventType.withName(formData.eventType),
            description = formData.description.getOrElse("")
        )

        event.insert()

        Ok(
            Json.toJson( event )
        ).as("text/text")
    }
}
