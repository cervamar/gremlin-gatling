package cz.cvut.fit.cervamar.gremlin.core

import javax.script.ScriptContext

import io.gatling.core.session.Session
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
  def eval(gremlinQuery: GremlinQuery, session: Session) : Object
}
class ExecutorQuery(graph: Graph) extends QueryExecutor {

  import ExecutorQuery._
  val engine = new GremlinGroovyScriptEngine()
  engine.put(GRAPH, graph)
  engine.put(TRAVERSAL, graph.traversal())

  override def eval(gremlinQuery: GremlinQuery, session: Session): AnyRef = {
      val bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE)
      //gremlinQuery.getAllVariables(session).foreach(x => bindings.put(x._1,x._2))
      gremlinQuery.getVariables.foreach(x => {
      bindings.put(x._1,x._2)
    })
      val plainQuery = gremlinQuery.getPlainQuery(session, graph.features.vertex.supportsNumericIds)
      println(plainQuery)
      engine.eval(plainQuery, bindings)
  }
}

object ExecutorQuery {
  val GRAPH = "graph"
  val TRAVERSAL = "g"
}

class ServerExecutor(client: Client) extends QueryExecutor {

  override def eval(gremlinQuery: GremlinQuery, session: Session): AnyRef = {
    client.submit(gremlinQuery.getPlainQuery(session, false), gremlinQuery.getAllVariables(session).asJava)
  }
}
