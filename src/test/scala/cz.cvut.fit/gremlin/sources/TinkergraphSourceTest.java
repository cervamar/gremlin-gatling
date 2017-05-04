package cz.cvut.fit.gremlin.sources;

import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.junit.Test;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.apache.tinkerpop.gremlin.structure.Direction.OUT;

/**
  * Created by cerva on 11/04/2017.
  */

public class TinkergraphSourceTest {
/*
  @Test
  public void insertVector() {
    Graph graph = TinkergraphSource.getEmptyInstance();
    String label = "name";
    Vertex paris = graph.addVertex(label);
    Consumer<Vertex> consumer = new Consumer<Vertex>() {
      public int matched = 0;

      @Override
      public void accept(Vertex vertex) {
          matched++;
      }
    };
    graph.vertices(paris.id());
    assert(consumer.matched);
  }*/

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

}