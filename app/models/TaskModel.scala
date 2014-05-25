package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

import play.api.db.slick.Config.driver.simple._

case class Task(id: Option[Int] = None, title: String)

object Task {
  implicit val implicitTaskWrites = new Writes[Task] {
    def writes(task: Task): JsValue = {
      Json.obj(
        "id" -> task.id.get,
        "title" -> task.title
      )
    }
  }
}

class TaskModel(tag: Tag) extends Table[Task](tag, "Task") {
   def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
   def title = column[String]("title")
   val task = Task.apply _
   def * = (id.?, title) <> (task.tupled, Task.unapply _)
}
