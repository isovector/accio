package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Task(id: Long, title: String)

object Task {
  implicit val taskWrites: Writes[Task] = (
    (JsPath \ "id").write[Long] and
    (JsPath \ "title").write[String]
  )(unlift(Task.unapply))
}
