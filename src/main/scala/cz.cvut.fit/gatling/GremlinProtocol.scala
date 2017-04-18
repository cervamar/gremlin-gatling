package cz.cvut.fit.gatling


import io.gatling.core.config.Protocol
import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine
import org.apache.tinkerpop.gremlin.structure.Graph

/**
  * Created by cerva on 13/04/2017.
  */
class GremlinProtocol (graph : Graph) extends Protocol {

  def call(gremlinQuery: String): Int = {
    val engine = new GremlinGroovyScriptEngine
    val results = Seq
    val bindings = engine.createBindings
    bindings.put("g", graph.traversal)
    bindings.put("results", results)
    try {
      engine.eval(gremlinQuery, bindings)
    }
      catch {
      case ex: Exception =>{
        println(ex.getMessage)
        return -1
      }
  }
    println(results)
    200
  }
}
