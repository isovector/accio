package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._

import scala.collection.mutable.HashMap

object TaskController extends Controller {
    var taskId: Long = 0
    val tasks = new HashMap[Long, String]


    def list = Action {
      Ok(
        tasks.foldRight("") {
          (kv, acc) => acc + kv._1  + " = " + kv._2 + "\n"
        }
      ).as("text/text")
    }

    def create = Action { implicit request =>
      val title: Option[String] = request.getQueryString("title")

      if (title isEmpty) {
        BadRequest
      }
      else {
        tasks += (taskId -> (title get))
        taskId = taskId + 1

        Ok
      }
    }

    def delete(id: Long) = Action {
      tasks -= id
     
      Ok
    }
}
