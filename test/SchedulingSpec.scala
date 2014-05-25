import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

import models.scheduling._
import scala.collection.immutable._
import com.github.nscala_time.time.Imports._

object Mock {
  def getWorkChunks(num: Int, start: DateTime = DateTime.now): Seq[WorkChunk] = {
    num match {
      case 0 => List()
      case n => 
        List(new WorkChunk(start, 1.hours)) ++ 
          getWorkChunks(n - 1, start + 1.hours)
    }
  }

  def makeTask(whenInFuture: Duration, duration: Duration) =
    new Task("test", DateTime.now + whenInFuture, duration, 0)

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
    makeTask(5.hours, 4.hours)
  )
}


@RunWith(classOf[JUnitRunner])
class SchedulingSpec extends Specification {
  "Scheduler" should {
    "not be able to finish 10h work in 5h time" in {
      new Scheduler(Mock.tasks10h, Mock.productivity5h).hasEnoughTime must beFalse
    }

    "1h work in 5h time" in {
      new Scheduler(Mock.tasks1h, Mock.productivity5h).hasEnoughTime must beTrue
    }

    "not have enough time before 5h deadline" in {
      new Scheduler(Mock.tasks5hDeadline, Mock.productivity10h).hasEnoughTime must beFalse
    }

    "successfully schedule" in {
      new Scheduler(Mock.tasks1h, Mock.productivity5h).doScheduling must beSome
    }
  }
}
