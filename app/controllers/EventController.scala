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
            eventId: Option[Int],
            taskId: Option[Int],
            startTime: String,
            endTime: String,
            where: Option[String],
            text: Option[String],
            eventType: String
        )

        val formData = Form(mapping(
            "id" -> optional(number),
            "task" -> optional(number),
            "start_date" -> text,
            "end_date" -> text,
            "where" -> optional(text),
            "text" -> optional(text),
            "eventType" -> text
        )(EventFormData.apply)(EventFormData.unapply)).bindFromRequest.get

        val task = formData.taskId.flatMap(
            id => TaskController.findByID(id)
        )

        val dateMatcher = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

        val event = new Event(
            id = formData.eventId,
            task = task,
            startTime = dateMatcher.parseDateTime(formData.startTime),
            endTime = dateMatcher.parseDateTime(formData.endTime),
            where = formData.where.getOrElse(""),
            eventType = EventType.withName(formData.eventType),
            description = formData.text.getOrElse("")
        )
        if (event.id.isEmpty) {
            event.insert()
        } else {
            DB.withSession { implicit session =>
                TableQuery[EventModel].filter(_.id === event.id.get).update(event)
            }
        }
        Ok(
            Json.toJson( event )
        ).as("text/text")
    }
}
