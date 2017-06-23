package cz.cvut.fit.gatling


import java.util
import java.util.concurrent._
import java.util.function.Consumer
import javax.script.{Bindings, CompiledScript, ScriptException}

import akka.actor.{ActorSystem, Props}
import cz.cvut.fit.gatling.protocol.GremlinProtocol
import cz.cvut.fit.gremlin.core.GremlinQuery
import io.gatling.commons.stats.Status
import io.gatling.core.action.{Action, ActionActor, ExitableActorDelegatingAction}
import io.gatling.core.session.{Expression, Session}
import io.gatling.core.stats.StatsEngine
import io.gatling.core.stats.message.ResponseTimings
import io.gatling.core.util.NameGen
import org.apache.tinkerpop.gremlin.driver.{Result, ResultSet}
import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine

/**
  * Created by cerva on 13/04/2017.
  */

object GremlinAction extends NameGen {

  def apply(requestName: Expression[String], gremlinQuery: GremlinQuery, protocol: GremlinProtocol, system: ActorSystem, statsEngine: StatsEngine, next: Action) = {
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
  var queryResult : util.List[Result] = null;
  var startTime = 0L;
  val executor = Executors.newScheduledThreadPool(2)

  var resolvedQuery = "sdf"

  def createJavaMap(getVariables: Map[String, Object]): util.Map[String, Object] = {
      val map = new util.HashMap[String, Object]()
    getVariables.foreach((entry) => map.put(entry._1, entry._2))
    map
  }
  override def execute(session: Session) = {
    //TODO:
    //resolvedQuery = gremlinQuery.getQuery.apply(session).get
    resolvedQuery = gremlinQuery.getPlainQuery(session, protocol.supportNumericIds)
    //println("User " + session.userId + " calling " + resolvedQuery)
    startTime = now()
    val response = protocol.client.submitAsync(resolvedQuery, createJavaMap(gremlinQuery.getVariables))
    response.thenAccept(new Consumer[ResultSet] {
      override def accept(t: ResultSet): Unit = {
        t.all().acceptEither(protocol.utility.timeoutAfter(20, TimeUnit.SECONDS), new Consumer[util.List[Result]] {
          override def accept(t: util.List[Result]): Unit = {
            val endTime = now()
            val timings = ResponseTimings(startTime, endTime)

            if(t == null) {
              //printResult(new util.ArrayList[Result](), timings.responseTime, resolvedQuery, Status("KO"))
              statsEngine.logResponse(session, requestName.apply(session).get, timings, Status("KO"), None, None)
            }
            else {
              //printResult(t, timings.responseTime, resolvedQuery, Status("OK"))
              statsEngine.logResponse(session, requestName.apply(session).get, timings, Status("OK"), None, None)
            }
            next ! session
          }
        })
        }
    })
  }

  def call(query : String): Int = {
    try {
      queryResult = protocol.client.submit(query).all().get();
      0
    }
      catch {
      case ex: Exception =>
        println(ex.getMessage)
        -1
      }
  }

  @inline
  private def now() = System.currentTimeMillis()

  @throws[ScriptException]
  def evaluate(compiledScript: CompiledScript, bindings: Bindings): Object = {
    compiledScript.eval(bindings)
  }

  def printResult(result: util.List[Result], time: Long, query: String, status: Status) {
    result.forEach( new Consumer[Result] {
      override def accept(t: Result): Unit = println(t);
    })
    println(query + "processed in time " + time + " with result " + status)
  }
}
