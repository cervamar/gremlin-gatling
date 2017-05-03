package cz.cvut.fit.gremlin.core

import javax.script.ScriptContext

import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine
import org.apache.tinkerpop.gremlin.structure.Graph

/**
  * Created on 5/3/2017.
  *
  * @author Marek.Cervak67
  */
class QueryBuilder(graph: Graph) {

  import QueryBuilder._
  val engine = new GremlinGroovyScriptEngine()
  engine.put(GRAPH, graph)
  engine.put(TRAVERSAL, graph.traversal())

  def shortestPath(vertexIdFrom:Object, vertexIdTo:Object) : EvaluableScriptQuery = {
    val bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE)
    bindings.put("vertexId", vertexIdFrom)
    bindings.put("vertexId2", vertexIdTo)
    new EvaluableScriptQuery("g.V(vertexId).repeat(out().simplePath()).until(hasId(vertexId2)).path().limit(1)", bindings, engine)
  }

}

object QueryBuilder {
  val GRAPH = "graph"
  val TRAVERSAL = "g"
}

