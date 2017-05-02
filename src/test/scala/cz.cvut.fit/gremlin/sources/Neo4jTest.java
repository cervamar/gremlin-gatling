package cz.cvut.fit.gremlin.sources;

import cz.cvut.fit.gremlin.utils.GraphUtils;
import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.junit.Before;
import org.junit.Test;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
  * Created by cerva on 11/04/2017.
  */

public class Neo4jTest {

  static Graph graph =  new GenericGraphSource("src/test/resources/neo4j-standalone.properties").openGraph();

  @Before
  public void prepareData() throws IOException {
    if (GraphUtils.isEmpty(graph)) {
      GraphUtils.importModern(graph);
    }
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
