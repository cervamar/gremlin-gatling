package cz.cvut.fit.gatling


import java.util
import java.util.function.Consumer

import akka.actor.{ActorSystem, Props}
import cz.cvut.fit.gatling.protocol.GremlinProtocol
import cz.cvut.fit.gremlin.core.GremlinQuery
import io.gatling.commons.stats.Status
import io.gatling.core.action.{Action, ActionActor, ExitableActorDelegatingAction}
import io.gatling.core.session.{Expression, Session}
import io.gatling.core.stats.StatsEngine
import io.gatling.core.stats.message.ResponseTimings
import io.gatling.core.util.NameGen
import org.apache.tinkerpop.gremlin.driver.Result
import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine

/**
  * Created by cerva on 13/04/2017.
  */

object GremlinAction extends NameGen {

  def apply(requestName: Expression[String], gremlinQuery: GremlinQuery, protocol: GremlinProtocol, system: ActorSystem, statsEngine: StatsEngine, next: Action): ExitableActorDelegatingAction = {
    val actor = system.actorOf(GremlinActionActor.props(requestName, gremlinQuery, protocol, statsEngine, next))
    new ExitableActorDelegatingAction(genName("Gremlin"), statsEngine, next, actor)
  }
}

object GremlinActionActor {
  def props(requestName: Expression[String], gremlinQuery: GremlinQuery, protocol: GremlinProtocol, statsEngine: StatsEngine, next: Action): Props =
    Props(new GremlinActionActor(requestName, gremlinQuery, protocol, statsEngine, next))
}

class GremlinActionActor(
     requestName: Expression[String],
     gremlinQuery: GremlinQuery,
     protocol: GremlinProtocol,
     val statsEngine: StatsEngine,
     val next: Action)  extends ActionActor {

  val engine = new GremlinGroovyScriptEngine
  var queryResult : util.List[Result] = _

  def createJavaMap(getVariables: Map[String, Object]): util.Map[String, Object] = {
      val map = new util.HashMap[String, Object]()
    getVariables.foreach((entry) => map.put(entry._1, entry._2))
    map
  }
  override def execute(session: Session): Unit = {
    val resolvedQuery = gremlinQuery.getPlainQuery(session, protocol.serverClient.isSupportNumericIds)
    val startTime = now()
    protocol.serverClient.submitAsync(resolvedQuery, createJavaMap(gremlinQuery.getVariables), new Consumer[util.List[Result]] {
      override def accept(result: util.List[Result]): Unit = {
        val endTime = now()
        val timings = ResponseTimings(startTime, endTime)
        if (result == null) {
          statsEngine.logResponse(session, requestName.apply(session).get, timings, Status("KO"), None, None)
          next ! session
        }
        else {
          statsEngine.logResponse(session, requestName.apply(session).get, timings, Status("OK"), None, None)
          printResult(result, timings.responseTime, resolvedQuery, Status("OK"))
          next ! session
        }
      }
    })
/*    try {
      val result = protocol.serverClient.submit(resolvedQuery, createJavaMap(gremlinQuery.getVariables))
      val endTime = now()
      val timings = ResponseTimings(startTime, endTime)

      statsEngine.logResponse(session, requestName.apply(session).get, timings, Status("OK"), None, None)
      printResult(result, timings.responseTime, resolvedQuery, Status("OK"))
    }
    catch {
      case ex: Exception => {
        val endTime = now()
        val timings = ResponseTimings(startTime, endTime)
        statsEngine.logResponse(session, requestName.apply(session).get, timings, Status("KO"), None, None)
      }
    }*/
    //next ! session
  }
  @inline
  private def now() = System.currentTimeMillis()

    def printResult(result: util.List[Result], time: Long, query: String, status: Status) {
    result.forEach( new Consumer[Result] {
      override def accept(t: Result): Unit = println(t)
    })
    println(query + "processed in time " + time + " with result " + status)
  }
}
