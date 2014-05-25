package models

import com.github.nscala_time.time.Imports._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.db.slick.Config.driver.simple._
import utils.DateConversions._

case class Event(
    id: Option[Int] = None,
    task: Option[Task] = None,
    when: DateTime,
    duration: Duration,
    where: String = "",
    description: String = "")

class EventModel(tag: Tag) extends Table[Event](tag, "Event") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def task = column[Option[Task]]("task")
    def start = column[DateTime]("start")
    def duration = column[Duration]("duration")
    def where = column[String]("where")
    def description = column[String]("description")

    def * = (id.?, task, start, duration, where, description) <> (Event.tupled, Event.unapply _)
}
