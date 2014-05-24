package models.scheduling


import scala.collection.immutable._
import com.github.nscala_time.time.Imports._

case class Task(
  name: String, 
  dueDate: DateTime, 
  duration: Duration, 
  importance: Int
)

case class WorkChunk(when: DateTime, duration: Duration)

class WorkChunkList(chunks: Seq[WorkChunk]) {
  def spendableTime: Duration = new Duration(
    chunks.foldLeft(0: Long)(
      (time, chunk) => time + chunk.duration.millis
    )
  )
}


class Scheduler(allTasks: Seq[Task], workChunks: Seq[WorkChunk]) {
  // Allows us to call WorkChunkList methods on work chunk lists
  implicit def listWrapper(chunks: Seq[WorkChunk]) = new WorkChunkList(chunks)


  val tasks = allTasks.sortBy { task => task.dueDate }

  object params {
    val minimumBufferTime = 1 hours
  }

  def getSpendableTimeDuring(start: DateTime, end: DateTime) = 
    workChunks.filter(
      chunk => start <= chunk.when && chunk.when < end
    ).spendableTime

  def hasEnoughTime: Boolean = {
    var curScheduleStart = DateTime.now
    var timeToSpend: Duration = 0.seconds

    for (task <- tasks) {
      timeToSpend += 
        getSpendableTimeDuring(curScheduleStart, task.dueDate) - 
          task.duration

      if (timeToSpend <= params.minimumBufferTime) {
        return false
      }

      curScheduleStart = task.dueDate
    }

    true
  }
}

