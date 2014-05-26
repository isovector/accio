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

case class TaskFormData(
    id: Option[Int],
    title: String,
    description: Option[String],
    dueDate: Option[String],
    estimatedTime: Option[Long]
)

object TaskController extends Controller {

    // TODO: get tasks for a specific user
    def list = Action {
        val existingTasks = DB.withSession { implicit session =>
            TableQuery[TaskModel].list
        }
        Ok(
            Json.toJson( existingTasks )
        ).as("text/text")
    }

    def create = Action { implicit request =>
        val formData = Form(mapping(
            "id" -> optional(number),
            "title" -> text,
            "description" -> optional(text),
            "dueDate" -> optional(text),
            "estimatedTime" -> optional(longNumber)
        )(TaskFormData.apply)(TaskFormData.unapply)).bindFromRequest.get

        val estimatedTime = formData.estimatedTime.map(
            time => new Duration(time)
        )

        try {
            val dueDate = formData.dueDate.map(
                date => DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parseDateTime(date)
            )

            if (formData.title isEmpty) {
                BadRequest
            } else {
                val task = new Task(
                    id = formData.id,
                    title = formData.title,
                    description = formData.description,
                    dueDate = dueDate,
                    estimatedTime = estimatedTime)
                if (task.id.isEmpty) {
                    task.insert()
                } else {
                    DB.withSession { implicit session =>
                        TableQuery[TaskModel].filter(_.id === task.id.get).update(task)
                    }
                }
                Ok( Json.toJson(task) ).as("text/text")
            }
        } catch {
            case e: IllegalArgumentException => BadRequest
        }
    }

    def delete(id: Int) = Action {
        val drop = DB.withSession { implicit session =>
            TableQuery[TaskModel].where(_.id === id).delete
        }
        Ok
    }

    def findByID(id: Int): Option[Task] =
        DB.withSession { implicit session =>
            TableQuery[TaskModel].filter(_.id === id).firstOption
        }
}
