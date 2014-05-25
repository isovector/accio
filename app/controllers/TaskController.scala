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
      
      val description = Form(
        "description" -> text
      ).bindFromRequest.get

      val dueDateString = Form(
        "dueDate" -> text
      ).bindFromRequest.get
      
      val estimatedTimeNumber = Form(
        "estimatedTime" -> number
      ).bindFromRequest.get

      val estimatedTime = new Duration(estimatedTimeNumber)

      // TODO: change to proper date time formatter
      var dueDate : DateTime = DateTime.now
      if (dueDateString != "") {
         val dateFormatter = DateTimeFormat.forPattern("yyyyMMdd")
         dueDate =  dateFormatter.parseDateTime(dueDateString)
      }

      if (title isEmpty) {
        BadRequest
      }
      else {
        val task = new Task(title = title, description = Some(description), dueDate = Some(dueDate), estimatedTime = Some(estimatedTime))
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
    
    def editTitle(id:Int) = Action { implicit session =>
      
      val title = Form(
        "title" -> text
      ).bindFromRequest.get

      if (title isEmpty) {
        BadRequest
      }
      else {
        Console.println(title)
        // TODO: put description and date
        val task = new Task(id = Some(id), title = title, description = Some(""))
        DB.withSession { implicit session =>
            TableQuery[TaskModel].filter(_.id === task.id.get).update(task)
        }

        Ok
      }
    }
}
