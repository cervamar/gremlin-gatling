package cz.cvut.fit.gremlin.sources

import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine
import org.apache.tinkerpop.gremlin.structure.Graph
import org.junit.Test

/**
  * Created on 4/20/2017.
  *
  * @author Marek.Cervak
  */
@Test
class ScalaGenericGraphSourceTest {

  val engine = new GremlinGroovyScriptEngine
  val graph = new GenericGraphSource("src/test/resources/tinkerpop-modern.properties").openGraph
  val results =  new java.util.ArrayList

  def compileQuery(resolvedQuery: String, graph: Graph) = {
    engine.compile(resolvedQuery)
  }

  def createBindings() = {
    val bindings = engine.createBindings
    bindings.put("g", graph.traversal())
    bindings.put("results", results)
    bindings
  }


  @Test
  def actorTest: Unit = {

        val query = "g.V(1).repeat(out().simplePath()).until(hasId(5)).path().limit(1).fill(results)"
        println(compileQuery(query, graph).eval(createBindings()))
        //print(result)
        print(results)
      }
}
