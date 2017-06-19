package cz.cvut.fit.gatling


import java.util
import java.util.function.Consumer
import javax.script.{Bindings, CompiledScript, ScriptException}

import akka.actor.{ActorSystem, Props}
import cz.cvut.fit.gatling.protocol.GremlinProtocol
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

  def apply(requestName: Expression[String], gremlinQuery: Expression[String], protocol: GremlinProtocol, system: ActorSystem, statsEngine: StatsEngine, next: Action) = {
    val actor = system.actorOf(GremlinActionActor.props(requestName, gremlinQuery, protocol, statsEngine, next))
    new ExitableActorDelegatingAction(genName("Gremlin"), statsEngine, next, actor)
  }
}

object GremlinActionActor {
  def props(requestName: Expression[String], gremlinQuery: Expression[String], protocol: GremlinProtocol, statsEngine: StatsEngine, next: Action): Props =
    Props(new GremlinActionActor(requestName, gremlinQuery, protocol, statsEngine, next))
}

class GremlinActionActor(
     requestName: Expression[String],
     gremlinQuery: Expression[String],
     protocol: GremlinProtocol,
     val statsEngine: StatsEngine,
     val next: Action)  extends ActionActor {

  val engine = new GremlinGroovyScriptEngine
  var queryResult : util.List[Result] = null;

  override def execute(session: Session) = {
    val resolvedQuery = gremlinQuery.apply(session).get
    val startTime = now()
    val resultCode = call(resolvedQuery)
    val endTime = now()
    printResult(queryResult, endTime - startTime, resolvedQuery)
    val timings = ResponseTimings(startTime, endTime)
    if (resultCode >= 200 && resultCode <= 299)
      statsEngine.logResponse(session, requestName.apply(session).get, timings, Status("OK"), None, None)
    else
      statsEngine.logResponse(session, requestName.apply(session).get, timings, Status("KO"), None, None)
    next ! session
  }

  def call(query : String): Int = {
    try {
      queryResult = protocol.client.submit(query).all().get();
      200
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

  def printResult(result: util.List[Result], time: Long, query: String) {
    result.forEach( new Consumer[Result] {
      override def accept(t: Result): Unit = println(t);
    })
    println(query + "processed in time " + time)
  }
}
