package cz.cvut.fit.gremlin.core

import javax.script.{Bindings, ScriptContext}

import org.apache.tinkerpop.gremlin.driver.Client
import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine
import org.apache.tinkerpop.gremlin.structure.Graph

import scala.collection.JavaConverters._

/**
  * Created on 5/3/2017.
  *
  * @author Marek.Cervak
  */

trait QueryExecutor {
  def eval(gremlinQuery: GremlinQuery) : Object
}
class ExecutorQuery(graph: Graph) extends QueryExecutor {

  import ExecutorQuery._
  val engine = new GremlinGroovyScriptEngine()
  engine.put(GRAPH, graph)
  engine.put(TRAVERSAL, graph.traversal())

  override def eval(gremlinQuery: GremlinQuery): AnyRef = {
      val bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE)
      gremlinQuery.getVariables.foreach(x => bindings.put(x._1,x._2))
      engine.eval(gremlinQuery.getQuery(), bindings)
  }
}

object ExecutorQuery {
  val GRAPH = "graph"
  val TRAVERSAL = "g"
}

class ServerExecutor(client: Client) extends QueryExecutor {
  override def eval(gremlinQuery: GremlinQuery): AnyRef = {
    client.submit(gremlinQuery.getQuery(), gremlinQuery.getVariables.asJava)
  }
}
