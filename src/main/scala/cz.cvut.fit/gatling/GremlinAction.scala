package cz.cvut.fit.gatling


import javax.script.{Bindings, CompiledScript, ScriptException}

import akka.actor.{ActorSystem, Props}
import cz.cvut.fit.gatling.protocol.GremlinProtocol
import io.gatling.commons.stats.Status
import io.gatling.core.action.{Action, ActionActor, ExitableActorDelegatingAction}
import io.gatling.core.session.{Expression, Session}
import io.gatling.core.stats.StatsEngine
import io.gatling.core.stats.message.ResponseTimings
import io.gatling.core.util.NameGen
import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine
import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils


/**
  * Created by cerva on 13/04/2017.
  */

object GremlinAction extends NameGen {

  def apply(requestName: Expression[String], gremlinQuery: Expression[String], protocol: GremlinProtocol, system: ActorSystem, statsEngine: StatsEngine, next: Action) = {
    val actor = system.actorOf(GremlinActionActor.props(requestName, gremlinQuery, protocol, statsEngine, next))
    new ExitableActorDelegatingAction(genName("Lambda"), statsEngine, next, actor)
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
  var queryResult = new Object

  def compileQuery(resolvedQuery: String, graph: Graph) = {
    engine.compile(resolvedQuery)
  }

  def createBindings() = {
    val bindings = engine.createBindings
    bindings.put("g", protocol.graph.traversal)
    bindings
  }

  override def execute(session: Session) = {
    val resolvedQuery = gremlinQuery.apply(session).get
    val compliedQuery = compileQuery(resolvedQuery, protocol.graph)
    val bindings = createBindings();
    val startTime = now()
    val resultCode = call(compliedQuery, bindings)
    val endTime = now()
    printResult(queryResult, endTime - startTime)
    val timings = ResponseTimings(startTime, endTime)
    if (resultCode >= 200 && resultCode <= 299)
      statsEngine.logResponse(session, requestName.apply(session).get, timings, Status("OK"), None, None)
    else
      statsEngine.logResponse(session, requestName.apply(session).get, timings, Status("KO"), None, None)
    next ! session
  }

  def call(compliedQuery: CompiledScript, bindings: Bindings): Int = {
    try {
      queryResult = evaluate(compliedQuery, bindings);
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

  def printResult(result: Object, time: Long) {
    System.out.println(IteratorUtils.asList(result).toString + " in time " + time)
  }
}
