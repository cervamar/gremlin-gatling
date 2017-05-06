package cz.cvut.fit.gremlin.core

import javax.script.{Bindings, ScriptContext}

import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine
import org.apache.tinkerpop.gremlin.structure.{Graph, Vertex}

/**
  * Created on 5/3/2017.
  *
  * @author Marek.Cervak67
  */
class GremlinQueryBuilder(graph: Graph) {

  import GremlinQueryBuilder._
  val engine = new GremlinGroovyScriptEngine()
  engine.put(GRAPH, graph)
  engine.put(TRAVERSAL, graph.traversal())

  def getLocalBindings(): Bindings = {
    engine.getBindings(ScriptContext.ENGINE_SCOPE)
  }

  def shortestPath(vertexIdFrom:Object, vertexIdTo:Object) : EvaluableScriptQuery = {
    val bindings = getLocalBindings
    bindings.put("vertexId", vertexIdFrom)
    bindings.put("vertexId2", vertexIdTo)
    new EvaluableScriptQuery("g.V(vertexId).repeat(out().simplePath()).until(hasId(vertexId2)).path().limit(1)", bindings, engine)
  }

  def addVertex(vertex:Vertex) : EvaluableScriptQuery = {
    val bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE)
    new EvaluableScriptQuery("graph.add", bindings, engine)
  }

  def query(query:String) : EvaluableScriptQuery = {
    val bindings = getLocalBindings
    new EvaluableScriptQuery(query, bindings, engine)
  }

}

object GremlinQueryBuilder {
  val GRAPH = "graph"
  val TRAVERSAL = "g"
}

