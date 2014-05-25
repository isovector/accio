package models

import com.github.nscala_time.time.Imports._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.db.slick.Config.driver.simple._
import utils.DateConversions._

object EventType extends Enumeration {
  val Normal    = Value("Normal")
  val Bewitched = Value("Bewitched")
  val WorkChunk = Value("WorkChunk") 
}

case class Event(
    id: Option[Int] = None,
    eventType: EventType.Value = EventType.Normal,
    task: Option[Task] = None,
    when: DateTime,
    duration: Duration,
    where: String = "",
    description: String = ""
)

class EventModel(tag: Tag) extends Table[Event](tag, "Event") {
    implicit def implicitEventTypeColumnMapper = 
      MappedColumnType.base[EventType.Value, String](
        etv => etv.toString,
        s => EventType.withName(s)
      )

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def eventType = column[EventType.Value]("eventType")
    def task = column[Option[Task]]("task")
    def start = column[DateTime]("start")
    def duration = column[Duration]("duration")
    def where = column[String]("where")
    def description = column[String]("description")

    def * = (
      id.?, 
      eventType, 
      task, 
      start, 
      duration, 
      where, 
      description
    ) <> (Event.tupled, Event.unapply _)
}
