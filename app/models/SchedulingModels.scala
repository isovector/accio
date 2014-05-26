package models.scheduling

import scala.collection.immutable._
import com.github.nscala_time.time.Imports._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current

import models._

// ========== DATA MODEL ==========

object SchedulingTypedefs {
  type WorkChunk = Event
}

import SchedulingTypedefs._

// Helper functions for dealing with lists of WorkChunks
class WorkChunkList(chunks: Seq[WorkChunk]) {
  // Seq[WorkChunk] => Duration
  def spendableTime = chunks.foldLeft[Duration](0 seconds)(
    (time, chunk) => time + chunk.duration
  )
}

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
  val necessaryTime = task.estimatedTime
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
    // val maxProjectTimePerSession = 4 hours

    // Minimum amount of time to get productive work done
    val rampUpPeriod = 20 minutes
  }

  // Only look at tasks with schedulable details
  val tasks = allTasks.filter(
    x => !x.dueDate.isEmpty && !x.estimatedTime.isEmpty
  ).filter(
    x => x.dueDate.get >= DateTime.now
  ).sortBy  { task => task.dueDate }

  val workChunks = allChunks.sortBy { chunk => chunk.startTime }


  // Gets the duration of all workchunks between two dates
  def getSpendableTimeDuring(start: DateTime, end: DateTime): Duration = 
    workChunks.filter(
      chunk => start <= chunk.startTime && chunk.startTime < end
    ).spendableTime


  // Determine if we have enough time in our workchunks to meet all of our
  // deadlines
  def hasEnoughTime: Either[SchedulingError, Boolean] = {
    var curScheduleStart = DateTime.now

    // Measures the amount of time in our workchunks that is not scheduled by
    // the time we've met this deadline
    var timeToSpend: Duration = 0.seconds

    for (task <- tasks) {
      timeToSpend += getSpendableTimeDuring(curScheduleStart, task.dueDate.get) - 
        task.estimatedTime.get

      if (timeToSpend < params.minBufferTime) {
        // Fail if we don't have enough of a time buffer to meet this deadline
        return Left(new NotEnoughTimeError(
          0.seconds - timeToSpend, // subtraction because time is negative
          Some(task)
        ))
      }

      curScheduleStart = task.dueDate.get
    }

    Right(true)
  }


  private def min(a: Duration, b: Duration): Duration =
    if (a < b) a else b


  private def createEvent(
      task: Task, 
      chunk: WorkChunk, 
      elapsed: Duration
  ): Event = {
        val start: DateTime = chunk.startTime + elapsed

        new Event(
          description = task.title,
          task = Some(task), 
          eventType = EventType.Scheduled, 
          startTime = start, 
          endTime = start + min(task.timeRemaining, chunk.duration)
        )
    }


  private def findBestTask(time: Duration): Option[Task] = {
    for (task <- tasks.filter(_.completedDuring.isEmpty)) {
      // Select the first task that isn't completed which we can finish
      // or make meaningful progress on
      if (task.timeRemaining <= time || 
          time >= params.rampUpPeriod) {
        return Some(task)
      }
    }

    None
  }


  // Schedule events into workchunks
  def doScheduling: Either[SchedulingError, Seq[Event]] = {
    var results: Seq[Event] = List()

    hasEnoughTime match {
      // If hasEnoughTime failed, propogate the error
      case Left(err) => return Left(err)
      case Right(_)  => // do nothing
    }


    // Loop through each work chunk
    for (chunk <- workChunks) {
      var timeRemaining = chunk.duration

      // Scala doesn't have break, so we will cheat =)
      object Break extends Exception { }

      try {
      // As long as we have time, keep fitting tasks into this chunk
      while (timeRemaining > 0.seconds) {
        val task = findBestTask(timeRemaining) match {
          case Some(x) => x

          // No more tasks can fit in this chunk, so exit
          case None    => throw Break
        }

        // Make an event to work on this task
        val event = createEvent(task, chunk, chunk.duration - timeRemaining)
        results :+= event

        // Update chunk and task for the work done
        val timeSpent = min(timeRemaining, task.timeRemaining)
        timeRemaining = timeRemaining - timeSpent
        task.timeRemaining = task.timeRemaining - timeSpent

        if (task.timeRemaining <= 0.seconds) {
          // Task was finished -- tell it when
          task.completedDuring = Some(chunk)
        }
      }} catch {
        case Break => // do nothing
      }
    }

    // Ensure all of our constraints are still met
    for (task <- tasks) {
      task.completedDuring match {
        // If a task is finished, make sure it was done before its due date
        case Some(chunk) => if (task.dueDate.get <= chunk.startTime) {
          return Left(new MissedDeadlineError(task))
        }

        // If a task wasn't finished, that's a problem
        case None => 
          return Left(new NotEnoughTimeError(task.timeRemaining, Some(task)))
      }
    }
    
    Right(results)
  }
}

