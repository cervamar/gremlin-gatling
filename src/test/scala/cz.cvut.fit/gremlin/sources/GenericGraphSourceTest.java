package cz.cvut.fit.gremlin.sources;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptException;

import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.junit.Test;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.hasId;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.out;

/**
  * Created by cerva on 11/04/2017.
  */

public class GenericGraphSourceTest {

  @Test
  public void getVertex() throws ScriptException {
    TinkerGraph g = TinkerFactory.createModern();
    GremlinGroovyScriptEngine engine = new GremlinGroovyScriptEngine();
    Graph graph = new GenericGraphSource("src/test/resources/tinkerpop-modern.properties").openGraph();
    Bindings bindings = engine.createBindings();
    bindings.put("g", graph.traversal());
    assert(IteratorUtils.count((Iterator) engine.eval("g.V(1).repeat(out().simplePath()).until(hasId(4)).path().limit(5)",bindings)) ==
            IteratorUtils.count(g.traversal().V(1).repeat(out().simplePath()).until(hasId(4)).path().limit(1)));
    System.out.println(IteratorUtils.asList(g.traversal().V(1).repeat(out().simplePath()).until(hasId(4)).path()));
  }


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
  }






}
