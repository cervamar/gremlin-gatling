package cz.cvut.fit.gremlin.sources;

import cz.cvut.fit.gremlin.utils.GraphUtils;
import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
  * Created by cerva on 11/04/2017.
  */

public class OrientDbTest {

  static Graph graph =  new GenericGraphSource("src/test/resources/orientDb.properties").openGraph();

  @Before
  public void prepareData() throws IOException {
    if (GraphUtils.isEmpty(graph)) {
      GraphUtils.importModern(graph);
    }
  }

  @Ignore
  @Test
  public void getVertex() throws ScriptException {
    //Graph graph = new OrientGraphFactory("remote:192.168.99.100:2424/test", "root", "rootpwd").getNoTx();
            //new OrientGraph("local:/home/gremlin/db/demo")
      assert (IteratorUtils.count(graph.vertices("9:2")) > 0);
      Iterator<Vertex> res = graph.vertices("9:2");
      String query = "g.vertices(\"9:3\")";
      ScriptEngine engine = new GremlinGroovyScriptEngine();
      Bindings bindings = engine.createBindings();
      bindings.put("g", graph);
      assert (IteratorUtils.count((Iterator) engine.eval(query, bindings)) > 0);
  }

  @Ignore
  @Test
    public void getVertexWithConfig() throws ScriptException {
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
  public void shortestPath() throws ScriptException {
    //graph.vertices()
    String query = "g.V().hasLabel(\"person\").has(\"name\", \"marko\").repeat(out().simplePath()).until(has(\"name\", \"ripple\")).path().limit(1)";
    ScriptEngine engine = new GremlinGroovyScriptEngine();
    List results = new ArrayList();
    Bindings bindings = engine.createBindings();
    bindings.put("g", graph.traversal());
    List result = IteratorUtils.asList(engine.eval(query, bindings));
    System.out.println(result);
    assert (result.size() > 0);

  }


}
