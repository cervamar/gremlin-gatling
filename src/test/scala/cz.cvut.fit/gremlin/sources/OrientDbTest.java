package cz.cvut.fit.gremlin.sources;

import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine;
import org.apache.tinkerpop.gremlin.orientdb.OrientGraphFactory;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.junit.Test;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.hasId;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.out;

/**
  * Created by cerva on 11/04/2017.
  */

public class OrientDbTest {

  @Test
  public void getVertex() throws ScriptException {
    Graph graph = new OrientGraphFactory("remote:192.168.99.100:2424/test", "root", "rootpwd").getNoTx();
            //new OrientGraph("local:/home/gremlin/db/demo")
      assert (IteratorUtils.count(graph.vertices("9:2")) > 0);
      Iterator<Vertex> res = graph.vertices("9:2");
      String query = "g.vertices(\"9:3\")";
      ScriptEngine engine = new GremlinGroovyScriptEngine();
      Bindings bindings = engine.createBindings();
      bindings.put("g", graph);
      assert (IteratorUtils.count((Iterator) engine.eval(query, bindings)) > 0);
  }


  @Test
  public void getVertexWithConfig() throws ScriptException {
    Graph graph =  new GenericGraphSource("src/test/resources/orientDb.properties").openGraph();
    //new OrientGraph("local:/home/gremlin/db/demo")
    assert (IteratorUtils.count(graph.vertices("9:2")) > 0);
    Iterator<Vertex> res = graph.vertices("9:2");
    String query = "g.vertices(\"9:3\")";
    ScriptEngine engine = new GremlinGroovyScriptEngine();
    Bindings bindings = engine.createBindings();
    bindings.put("g", graph);
    assert (IteratorUtils.count((Iterator) engine.eval(query, bindings)) > 0);
  }

/*
  @Test
  public void insertVector() throws ScriptException {
    Graph graph = new GenericGraphSource("src/test/resources/tinkerpop-modern.properties").openGraph();
    String query = "g.V(1).repeat(out().simplePath()).until(hasId(5)).path().limit(1)";//.fill(results)";
    GremlinGroovyScriptEngine engine = new GremlinGroovyScriptEngine();
    List results = new ArrayList();
    Bindings bindings = engine.createBindings();
    bindings.put("g", graph.traversal());
    //bindings.put("results", results);
    CompiledScript compliedQuery = engine.compile(query);
    System.out.println(compliedQuery.eval(bindings));
*/
    //System.out.println(results);

    /*String label = "france";
    graph.addVertex(label);*/
/*     paris = graph + label
    graph + "germany"
    val vertices = graph.V()
    assert(graph.V(paris.id()).exists())
    val labeledVertices = graph.V.hasLabel(label)
    println(labeledVertices.count.head())
    println("paris has id" + paris.id())
    println(graph.V.value("id").toList)
    graph.tx().commit()
    graph.close()
  }*/

}
