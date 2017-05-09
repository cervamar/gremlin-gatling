package cz.cvut.fit.gremlin.sources;

import com.steelbridgelabs.oss.neo4j.structure.Neo4JElementIdProvider;
import com.steelbridgelabs.oss.neo4j.structure.Neo4JGraph;
import com.steelbridgelabs.oss.neo4j.structure.Neo4JGraphConfigurationBuilder;
import com.steelbridgelabs.oss.neo4j.structure.Neo4JGraphFactory;
import com.steelbridgelabs.oss.neo4j.structure.providers.DatabaseSequenceElementIdProvider;
import cz.cvut.fit.gremlin.utils.GraphUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;

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

  static Configuration configuration = Neo4JGraphConfigurationBuilder.connect("192.168.99.100", "neo4j", "root")
          .withName("neo4j-bolt")
          .withElementIdProvider(cz.cvut.fit.gremlin.sources.ElementIdProvider.class)
          //.withName(s"neo4j-bolt-${UUID.randomUUID.toString}")
          .build();
  //static Graph graph =  new GenericGraphSource("src/test/resources/neo4j-remote.properties").openGraph();
  static Graph graph = Neo4JGraphFactory.open(configuration);

 // static Driver driver = GraphDatabase.driver("bolt://192.168.99.100", AuthTokens.basic("neo4j", "admin"));
  //static Neo4JElementIdProvider<?> provider = new DatabaseSequenceElementIdProvider(driver);
 // static Neo4JElementIdProvider<?>  provider = new ElementIdProvider();
 // static Graph graph = new Neo4JGraph(driver, provider, provider);

  @Before
  public void prepareData() throws IOException {
/*    if (GraphUtils.isEmpty(graph)) {
      GraphUtils.importModern(graph);
    }*/
    Vertex vertex = graph.addVertex(T.label, "person", "name", "marko");
    Vertex vertex2 = graph.addVertex(T.label, "person", "name", "ripple");
    vertex.addEdge("knows", vertex2);
    graph.tx().commit();

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

  @AfterClass
  public static void close() throws Exception {
    graph.close();
  }

}
