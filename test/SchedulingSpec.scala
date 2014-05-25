import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

import models._
import models.scheduling._
import scala.collection.immutable._
import com.github.nscala_time.time.Imports._

object Mock {
  def getWorkChunks(num: Int, start: DateTime = DateTime.now): Seq[Event] = {
    num match {
      case 0 => List()
      case n => 
        List(new Event(when = start, duration = 1.hours, eventType = EventType.WorkChunk)) ++ 
          getWorkChunks(n - 1, start + 1.hours)
    }
  }

  def makeTask(whenInFuture: Duration, duration: Duration) =
    new Task(
      title = "test", 
      dueDate = Some(DateTime.now + whenInFuture), 
      estimatedTime = Some(duration)
    )

  val productivity1h = getWorkChunks(1)
  val productivity5h = getWorkChunks(5)
  val productivity10h = getWorkChunks(10)

  val tasks1h = List(
    makeTask(24.hours, 1.hours)
  )

  val tasks10h = List(
    makeTask(24.hours, 5.hours), 
    makeTask(48.hours, 5.hours)
  )

  val tasks5hDeadline = List(
    makeTask(5.hours, 5.hours)
  )

  val tasksLotsOfSmall = List(
    makeTask(24.hours, 30.minutes),
    makeTask(24.hours, 10.minutes),
    makeTask(24.hours, 10.minutes),
    makeTask(24.hours, 10.minutes)
  )

  val tasksLotsOfSmallAndBig = List(
    makeTask(24.hours, 30.minutes),
    makeTask(24.hours, 10.minutes),
    makeTask(24.hours, 10.minutes),
    makeTask(24.hours, 10.minutes),
    makeTask(8.hours, 2.hours)
  )
}


@RunWith(classOf[JUnitRunner])
class SchedulingSpec extends Specification {
/*  "Scheduler" should {
    "not have enough time for 10h work in 5h time" in {
      new Scheduler(Mock.tasks10h, Mock.productivity5h).hasEnoughTime must beLeft
    }

    "not have enough time before 5h deadline" in {
      new Scheduler(Mock.tasks5hDeadline, Mock.productivity10h).hasEnoughTime must beLeft
    }

    "schedule 1h task in 5h time" in {
      new Scheduler(Mock.tasks1h, Mock.productivity5h).doScheduling must beRight
    }

    "schedule small tasks into 1h" in {
      val result = new Scheduler(Mock.tasksLotsOfSmall, Mock.getWorkChunks(2)).doScheduling
      result match {
        case Left(_)  => result must beRight
        case Right(events) => events must haveSize(4)
      }
    }

    "schedule small tasks + big into 3 events" in {
      val result = new Scheduler(Mock.tasksLotsOfSmallAndBig, Mock.getWorkChunks(5)).doScheduling
      result match {
        case Left(_)  => result must beRight
        case Right(events) => events must haveSize(6)
      }
    }
  }*/
}
