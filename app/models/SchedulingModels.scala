package models.scheduling

import scala.collection.immutable._
import com.github.nscala_time.time.Imports._

// Something we want to accomplish
case class Task(
  name: String, 
  dueDate: DateTime, 
  var duration: Duration, 
  importance: Int
)

// Scheduled time for Getting Shit Done
case class WorkChunk(when: DateTime, duration: Duration)

// Helper functions for dealing with lists of WorkChunks
class WorkChunkList(chunks: Seq[WorkChunk]) {
  // Seq[WorkChunk] => Duration
  def spendableTime = chunks.foldLeft[Duration](0 seconds)(
    (time, chunk) => time + chunk.duration
  )
}

// A scheduled event
case class Event(task: Task, when: DateTime, duration: Duration)


class Scheduler(allTasks: Seq[Task], allChunks: Seq[WorkChunk]) {
  // Allows us to call WorkChunkList methods on work chunk lists
  implicit def listWrapper(chunks: Seq[WorkChunk]) = new WorkChunkList(chunks)

  // Parameters for tweaking the scheduling
  object params {
    // Need to have at least this much time free at any given point in the
    // scheduling algo (ensure we have time in case of emergencies)
    val minBufferTime = 1 hours

    // Only schedule this much time per project per day (prevent burnouts)
    val maxProjectTimePerSession = 4 hours
  }


  val tasks      = allTasks.sortBy  { task => task.dueDate }
  val workChunks = allChunks.sortBy { chunk => chunk.when }

  // Gets the duration of all workchunks between two dates
  def getSpendableTimeDuring(start: DateTime, end: DateTime): Duration = 
    workChunks.filter(
      chunk => start <= chunk.when && chunk.when < end
    ).spendableTime


  // Determine if we have enough time in our workchunks to meet all of our
  // deadlines
  def hasEnoughTime: Boolean = {
    var curScheduleStart = DateTime.now

    // Measures the amount of time in our workchunks that is not scheduled by
    // the time we've met this deadline
    var timeToSpend: Duration = 0.seconds

    for (task <- tasks) {
      timeToSpend += getSpendableTimeDuring(curScheduleStart, task.dueDate) - 
        task.duration

      if (timeToSpend <= params.minBufferTime) {
        // Fail if we don't have enough of a time buffer to meet this deadline
        return false
      }

      curScheduleStart = task.dueDate
    }

    true
  }

  private def min(a: Duration, b: Duration): Duration =
    if (a < b) a else b

  private def createEvent(task: Task, chunk: WorkChunk): Event =
    new Event(task, chunk.when, min(task.duration, chunk.duration))

  // Schedule events into workchunks
  def doScheduling: Option[Seq[Event]] = {
    var results: Seq[Event] = List()

    if (!hasEnoughTime) {
      return None
    }

    var available = workChunks
    var toSchedule = tasks

    while (toSchedule.length != 0) {
      // Using "def" so we get a reference to toSchedule.head
      def task = toSchedule.head

      val chunkOption = available.headOption
      if (chunkOption == None) {
        // Ran out of chunks to schedule in
        return None
      }

      val chunk = chunkOption.get
      if (task.dueDate < chunk.when) {
        // Missed our deadline =(
        return None
      }

      available = available.tail

      // Schedule this work chunk as an event
      val event = createEvent(task, chunk)
      results :+= event

      task.duration = task.duration - event.duration
      if (task.duration <= 0.seconds) {
        // Task is finished, onwards!
        toSchedule = toSchedule.tail
      }
    }

    Some(results)
  }
}

