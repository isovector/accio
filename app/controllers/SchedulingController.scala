package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.data._
import play.api.data.Forms._

import play.api.db.slick.DB
import play.api.Play.current
import com.github.nscala_time.time.Imports._
import play.api.db.slick.Config.driver.simple._

import models._
import models.scheduling._

object SchedulingController extends Controller {
    def create = Action { implicit request =>
        val chunks = DB.withSession { implicit session =>
            TableQuery[EventModel].list
        }.filter(_.eventType == EventType.WorkChunk)

        val tasks = DB.withSession { implicit session =>
            TableQuery[TaskModel].list
        }

        new Scheduler(tasks, chunks).doScheduling match {
            case Left(error) => BadRequest(error.toString)
            case Right(events) => {
                events.map(event => event.insert())
                Ok(Json.toJson(events)).as("text/text")
            }
        }
    }
}
