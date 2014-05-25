package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

import play.api.db.slick.Config.driver.simple._
import com.github.nscala_time.time.Imports._

case class Task(id: Option[Int] = None, title: String, description: Option[String] = None, dueDateTimeStamp : Option[Long] = None)

object Task {
  implicit val implicitTaskWrites = new Writes[Task] {
    def writes(task: Task): JsValue = {
      Json.obj(
        "id" -> task.id.get,
        "title" -> task.title,
        "description" -> task.description.get,
        "dueDate" -> task.dueDateTimeStamp.get
      )
    }
  }

}

class TaskModel(tag: Tag) extends Table[Task](tag, "Task") {
   def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
   def title = column[String]("title")
   def description = column[String]("description")
   def dueDateTimeStamp = column[Long]("dueDateTimeStamp")
   val task = Task.apply _
   def * = (id.?, title, description.?, dueDateTimeStamp.?) <> (task.tupled, Task.unapply _)
}
