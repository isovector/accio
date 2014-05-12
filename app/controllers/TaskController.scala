package controllers

import play.api._
import play.api.libs.json._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._

import scala.collection.mutable.HashMap

object TaskController extends Controller {
    var taskId: Long = 0
    val tasks = new HashMap[Long, Task]


    def list = Action {
      Ok(
        Json.toJson(tasks.toMap.map {
          case(k, v) => (k toString, v)
        })
      ).as("text/text")
    }

    def create = Action { implicit request =>
      val title = Form(
        "title" -> text
      ).bindFromRequest.get

      if (title isEmpty) {
        BadRequest
      }
      else {
        tasks += (taskId -> new Task(taskId, title))
        taskId = taskId + 1

        Ok
      }
    }

    def delete(id: Long) = Action {
      tasks -= id
     
      Ok
    }
}
