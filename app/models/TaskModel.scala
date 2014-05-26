package models

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import com.github.nscala_time.time.Imports._

import controllers.TaskController
import utils.DateConversions._

case class Task(id: Option[Int] = None, title: String, description: Option[String] = None,
         dueDate: Option[DateTime] = None, estimatedTime: Option[Duration] = None)

object Task {
    implicit val implicitDurationWrites = new Writes[Duration] {
        def writes(duration: Duration): JsValue = JsNumber(duration.millis)
    }

    val dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

    implicit val implicitTaskWrites = new Writes[Task] {
        def writes(task: Task): JsValue = {
            Json.obj(
                "id" -> task.id.get,
                "title" -> task.title,
                "description" -> task.description,
                "dueDate" -> task.dueDate.map(date => dateFormatter.print(date.getMillis)),
                "estimatedTime" -> task.estimatedTime)
        }
    }

    implicit val implicitTaskColumnMapper = MappedColumnType.base[Task, Int](
        t => t.id.get,
        i => TaskController.findByID(i).get
    )

  class RichTask(underlying: Task) {
    var timeRemaining: Duration = underlying.estimatedTime.getOrElse(1.hours)
    var completedDuring: Option[Event] = None
  }

  implicit def task2RichTask(underlying: Task): RichTask = new RichTask(underlying)
}

class TaskModel(tag: Tag) extends Table[Task](tag, "Task") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")
    def description = column[Option[String]]("description")
    def dueDate = column[Option[DateTime]]("dueDate")
    def estimatedTime = column[Option[Duration]]("estimatedTime")
    val task = Task.apply _
    def * = (id.?, title, description, dueDate, estimatedTime) <> (task.tupled, Task.unapply _)
}
