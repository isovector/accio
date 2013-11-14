package controllers

import play.api.libs.iteratee.{Step, Enumerator, Iteratee, Concurrent}
import scala.concurrent.{Promise, ExecutionContext, Future}
import play.api.libs.concurrent.Execution.Implicits._

import play.api._
import play.api.mvc._

object ChatExample extends Controller {
    // make a broadcast channel to let us talk to all connected clients
    val (broadcast, bchannel) = Concurrent.broadcast[String]

    // called when we get a client
    def index = WebSocket.using[String] { request => 
        // make a channel to only talk to our client
        val (rawout, channel) = Concurrent.broadcast[String]

        // route broadcast channel through our output channel
        val out = broadcast interleave rawout
        
        val in = Iteratee.foreach[String] { msg =>
            // this block is called for every message we receive
            println(msg)
            
            // echo it back through the broadcast (ie to everyone)
            bchannel push msg
        }.mapDone { _ =>
            println("Disconnected")
        }

        // return our io stream
        (in, out)
    }
}