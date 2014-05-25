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
          when: String,
          duration: Long,
          where: Option[String],
          description: Option[String],
          eventType: String
        )

        val formData = Form(
          mapping(
            "task" -> optional(number),
            "when" -> text,
            "duration" -> longNumber,
            "where" -> optional(text),
            "description" -> optional(text),
            "eventType" -> text
          )(EventFormData.apply)(EventFormData.unapply)
        ).bindFromRequest.get

        val task = formData.taskId match {
            case Some(id) =>
               TaskController.findByID(id)
            case None =>
                None
        }

        val event = new Event(
            task = task,
            when = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parseDateTime(formData.when),
            duration = new Duration(formData.duration),
            where = formData.where.getOrElse(""),
            eventType = EventType.withName(formData.eventType),
            description = formData.description.getOrElse("")
        )

        event.insert()

        Ok
    }
}
