package cz.cvut.fit.cervamar.gatling.action

import java.util

import cz.cvut.fit.cervamar.gatling.ResultCheck
import cz.cvut.fit.cervamar.gatling.protocol.{GremlinProtocol, ResultConsumer}
import cz.cvut.fit.cervamar.gatling.util.ScalaUtils
import cz.cvut.fit.cervamar.gremlin.core.GremlinQuery
import io.gatling.commons.stats.{KO, OK}
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
      case scala.util.Failure(msg) => {
        logger.error("Something went wrong during simulation {}, with cause {}", requestName, msg.getMessage)
        KO
      }
    }
    requestName.apply(session).foreach { resolvedRequestName =>
      statsEngine.logResponse(session, resolvedRequestName, timing, status, None, None)
    }
  }

  override def name: String = genName("gremlinQuery")
}

case class GremlinExecuteAction(
                                 requestName: Expression[String],
                                 gremlinQuery: GremlinQuery,
                                 checks: List[ResultCheck],
                                 extractor: Option[Extractor],
                                 protocol: GremlinProtocol,
                                 statsEngine: StatsEngine,
                                 next: Action) extends GremlinAction {

  def resolveQuery(session: Session): String = {
    gremlinQuery.getPlainQuery(session, protocol.serverClient.isSupportNumericIds)
  }

  override def execute(session: Session): Unit = {
    val startTime: Long = now()
    val tried = Try(doAction(session, startTime))
    if (tried.isFailure) {
      log(startTime, now(), tried, requestName, session, statsEngine)
      next ! session.markAsFailed
    }
  }

  def doAction(session: Session, startTime: Long): Unit = {
    val resolvedQuery = resolveQuery(session)
    logger.debug("Calling {}", resolvedQuery)
    protocol.serverClient.submitAsync(resolvedQuery, ScalaUtils.createJavaMap(gremlinQuery.getVariables),
      new ResultConsumer {
        override def accept(result: util.List[Result]): Unit = {
          logger.debug("Result is {}", result)
          performChecks(session, startTime, result.asScala.toList)
        }

        override def acceptError(t: Throwable): Unit = {
          log(startTime, now(), Try(t), requestName, session, statsEngine)
          next ! session.markAsFailed
        }
      })
  }


  @inline
  private def now() = ClockSingleton.nowMillis

  def extractValue(session: Session, results: List[Result]): Session = {
    if (extractor.isDefined) {
      val value = Try(extractor.get.extractionMethod(results))
      value match {
        case Success(v) => return session.set(extractor.get.key, v)
        case Failure(ex) =>
          logger.warn("Problem during extracting value with key: {}", extractor.get.key)
          next ! session.markAsFailed
      }
    }
    session
  }

  private def performChecks(session: Session, start: Long, results: List[Result]): Unit = {
    val (modifySession, error) = Check.check(results, session, checks)
    var newSession = modifySession(session)
    error match {
      case Some(failure) =>
        requestName.apply(session).map { resolvedRequestName =>
          statsEngine.logResponse(session, resolvedRequestName, ResponseTimings(start, now()), KO, None, None)
        }
        next ! newSession.markAsFailed
      case _ =>
        newSession = extractValue(newSession, results)
        log(start, now(), scala.util.Success(""), requestName, session, statsEngine)
        next ! newSession
    }
  }

}
