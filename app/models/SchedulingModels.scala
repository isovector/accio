package models.scheduling

import scala.collection.immutable._
import com.github.nscala_time.time.Imports._


// ========== DATA MODEL ==========

// Something we want to accomplish
class Task(
    cname: String, 
    cdueDate: DateTime, 
    cduration: Duration, 
    cimportance: Int) {
  val name = cname
  val dueDate = cdueDate
  val duration = cduration
  var timeRemaining = cduration
  val importance = cimportance
}

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


// ========== ERRORS ==========

// Base trait for errors
trait SchedulingError {
  val error: String
  val offendingTask: Option[Task]
  val necessaryTime: Option[Duration]
}

// Not enough time to possibly schedule everything
class NotEnoughTimeError(time: Duration, task: Option[Task])
    extends SchedulingError {
  val error = "Not enough time"
  val offendingTask = task
  val necessaryTime = Some(time)
}

// Scheduling failed to meet the deadline despite having enough time
// (shouldn't happen until we start playing with maxProjectTimePerSession)
class MissedDeadlineError(task: Task) extends SchedulingError {
  val error = "Missed deadline"
  val offendingTask = Some(task)
  val necessaryTime = Some(task.duration)
}


// ========== SCHEDULING ==========

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
  def hasEnoughTime: Either[SchedulingError, Boolean] = {
    var curScheduleStart = DateTime.now

    // Measures the amount of time in our workchunks that is not scheduled by
    // the time we've met this deadline
    var timeToSpend: Duration = 0.seconds

    for (task <- tasks) {
      timeToSpend += getSpendableTimeDuring(curScheduleStart, task.dueDate) - 
        task.duration

      if (timeToSpend <= params.minBufferTime) {
        // Fail if we don't have enough of a time buffer to meet this deadline
        Left(new NotEnoughTimeError(
          0.seconds - timeToSpend, // subtraction because time is negative
          Some(task)
        ))
      }

      curScheduleStart = task.dueDate
    }

    Right(true)
  }


  private def min(a: Duration, b: Duration): Duration =
    if (a < b) a else b


  private def createEvent(task: Task, chunk: WorkChunk): Event =
    new Event(task, chunk.when, min(task.timeRemaining, chunk.duration))


  // Schedule events into workchunks
  def doScheduling: Either[SchedulingError, Seq[Event]] = {
    var results: Seq[Event] = List()

    hasEnoughTime match {
      // If hasEnoughTime failed, propogate the error
      case Left(err) => return Left(err)
    }

    var available = workChunks
    var toSchedule = tasks

    // TODO(sandy): this will do stupid things for task.duration << chunk.duration
    while (toSchedule.length != 0) {
      // Using "def" so we get a reference to toSchedule.head
      def task = toSchedule.head

      val chunkOption = available.headOption
      if (chunkOption == None) {
        // Ran out of chunks to schedule in
        return Left(new NotEnoughTimeError(task.duration, Some(task)))
      }

      val chunk = chunkOption.get
      if (task.dueDate < chunk.when) {
        // Missed our deadline =(
        return Left(new MissedDeadlineError(task))
      }

      available = available.tail

      // Schedule this work chunk as an event
      val event = createEvent(task, chunk)
      results :+= event

      task.timeRemaining = task.timeRemaining - event.duration
      if (task.timeRemaining <= 0.seconds) {
        // Task is finished, onwards!
        toSchedule = toSchedule.tail
      }
    }

    Right(results)
  }
}

