package controllers

import play.api._
import play.api.libs.json._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.db.slick.DB
import play.api.Play.current

import models._

import scala.collection.mutable.HashMap
import play.api.db.slick.Config.driver.simple._

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
      val title = Form(
        "title" -> text
      ).bindFromRequest.get

      if (title isEmpty) {
        BadRequest
      }
      else {
        Console.println(title)
        val task = new Task(title = title)
        DB.withSession { implicit session =>
            TableQuery[TaskModel] += task
        }

        Ok
      }
    }

    def delete(id: Int) = Action {
      val drop = DB.withSession { implicit session =>
          TableQuery[TaskModel].where(_.id === id).delete
      }
      Ok
    }
}
