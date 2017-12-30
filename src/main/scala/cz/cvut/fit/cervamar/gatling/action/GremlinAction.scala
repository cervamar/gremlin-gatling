package cz.cvut.fit.cervamar.gatling.action

import java.util
import java.util.function.Consumer

import cz.cvut.fit.cervamar.gatling.ResultCheck
import cz.cvut.fit.cervamar.gatling.protocol.GremlinProtocol
import cz.cvut.fit.cervamar.gremlin.core.GremlinQuery
import io.gatling.commons.stats.{KO, OK, Status}
import io.gatling.commons.util.ClockSingleton
import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.check.Check
import io.gatling.core.session.{Expression, Session}
import io.gatling.core.stats.StatsEngine
import io.gatling.core.stats.message.ResponseTimings
import io.gatling.core.util.NameGen
import org.apache.tinkerpop.gremlin.driver.Result

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

/**
  * Created by cerva on 13/04/2017.
  */

trait GremlinAction extends ChainableAction with NameGen {

  def log(start: Long, end: Long, tried: Try[_], requestName: Expression[String], session: Session, statsEngine: StatsEngine): Unit = {
    val timing = ResponseTimings(start, end)
    val status = tried match {
      case scala.util.Success(_) => OK
      case scala.util.Failure(_) => KO
    }
    requestName.apply(session).foreach { resolvedRequestName =>
      statsEngine.logResponse(session, resolvedRequestName, timing, status, None, None)
    }
  }

  override def name: String = genName("gremlinQuery")
}

case class GremlinExecuteAction (
     requestName: Expression[String],
     gremlinQuery: GremlinQuery,
     checks: List[ResultCheck],
     extractor: Option[Extractor],
     protocol: GremlinProtocol,
     statsEngine: StatsEngine,
     next: Action) extends GremlinAction {

  override def execute(session: Session): Unit = {
    val resolvedQuery = gremlinQuery.getPlainQuery(session, protocol.serverClient.isSupportNumericIds)
    val startTime = now()
    protocol.serverClient.submitAsync(resolvedQuery, createJavaMap(gremlinQuery.getVariables), new Consumer[util.List[Result]] {
      override def accept(result: util.List[Result]): Unit = {
        if (result == null) {
          log(startTime, now(), scala.util.Failure(new Throwable), requestName, session, statsEngine)
          next ! session
        }
        else {
          performChecks(session, startTime, result.asScala.toList)
        }
      }
    })
  }
  @inline
  private def now() = ClockSingleton.nowMillis

  def createJavaMap(getVariables: Map[String, Object]): util.Map[String, Object] = {
    val map = new util.HashMap[String, Object]()
    getVariables.foreach((entry) => map.put(entry._1, entry._2))
    map
  }

  def printResult(result: util.List[Result], time: Long, query: String, status: Status) {
    result.forEach( new Consumer[Result] {
      override def accept(t: Result): Unit = println(t)
    })
    println(query + "processed in time " + time + " with result " + status)
  }

  private def performChecks(session: Session, start: Long, results: List[Result]) = {
   println("neco delam")
    val (modifySession, error) = Check.check(results, session, checks)
    var newSession = modifySession(session)
    error match {
      case Some(failure) =>
        requestName.apply(session).map { resolvedRequestName =>
          statsEngine.logResponse(session, resolvedRequestName, ResponseTimings(start, now()), KO, None, None)
        }
        next ! newSession.markAsFailed
      case _ =>
        if(extractor.isDefined) {
          println(results)
          val value = Try(extractor.get.extractionMethod(results))
          value match {
            case Success(v) => newSession = newSession.set(extractor.get.key, v)
            case Failure(ex) => {
              println(s"Problem: ${ex.getMessage}")
            }
          }
        }
        log(start, now(), scala.util.Success(""), requestName, session, statsEngine)
        next ! newSession
    }
  }

}
