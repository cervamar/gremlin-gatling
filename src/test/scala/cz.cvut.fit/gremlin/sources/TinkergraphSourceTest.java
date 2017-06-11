package cz.cvut.fit.gremlin.sources;

import cz.cvut.fit.gremlin.utils.TestSourceProvider;
import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.io.IoCore;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.junit.Test;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.apache.tinkerpop.gremlin.structure.Direction.OUT;

/**
  * Created by cerva on 11/04/2017.
  */

public class TinkergraphSourceTest {

  @Test
  public void shortestPath() throws ScriptException {
    Graph graph = TinkerFactory.createModern();
    String query = "g.V(1).repeat(out().simplePath()).until(hasId(5)).path().limit(1).fill(results)";
    ScriptEngine engine = new GremlinGroovyScriptEngine();
    List results = new ArrayList();
    Bindings bindings = engine.createBindings();
    bindings.put("g", graph.traversal());
    bindings.put("results", results);
    engine.eval(query, bindings);
    System.out.println(results);
  }

  @Test
  public void getVertex() throws ScriptException {
    Graph graph = TinkerFactory.createModern();
    graph.vertices(1);
    String query = "g.vertices(1)";
    ScriptEngine engine = new GremlinGroovyScriptEngine();
    Bindings bindings = engine.createBindings();
    bindings.put("g", graph);
    assert (IteratorUtils.count((Iterator) engine.eval(query, bindings)) > 0);
  }

  @Test
  public void createEdge() throws ScriptException {
    Graph graph = TinkerFactory.createModern();
    Vertex to = graph.vertices(6).next();
    Vertex from = graph.vertices(1).next();
    assert(IteratorUtils.count(from.edges(OUT,"wish")) == 0);
    System.out.println(IteratorUtils.asList(from.edges(OUT)));
    from.addEdge("wish", to);
    System.out.println(IteratorUtils.asList(from.edges(OUT)));
    assert(IteratorUtils.count(from.edges(OUT,"wish")) == 1);
  }

  @Test
  public void moveGraphFromOrientToTinker() throws Exception {
    TestSourceProvider.GraphInMemorySource to = new TestSourceProvider.GraphInMemorySource("src/test/resources/tinkerpop-modern.properties");
    TestSourceProvider.GraphInMemorySource from = new TestSourceProvider.GraphInMemorySource("src/test/resources/orientDb-inmemory.properties");
    to.initGraph();
    from.initGraph();
    from.fill();
    String path = "src/test/resources/test.kryo";
    from.getGraph().io(IoCore.gryo()).writeGraph(path);
    to.getGraph().io(IoCore.gryo()).readGraph(path);
    List<Vertex> vertices = IteratorUtils.asList(to.getGraph().vertices());
    for (Vertex vertex : vertices) {
      System.out.println(vertex.id());
    }
    from.clean();
    to.clean();
  }
  


}