package cz.cvut.fit.gremlin.sources;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import cz.cvut.fit.gremlin.utils.TestSourceProvider;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.io.IoCore;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.junit.Ignore;
import org.junit.Test;

import static org.apache.tinkerpop.gremlin.structure.Direction.OUT;

/**
  * Created by cerva on 11/04/2017.
  */

@Ignore
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

  @Test
  public void random() throws FileNotFoundException, ExecutionException, InterruptedException {
    Cluster cluster = Cluster.build(new File("C:\\Users\\marek.cervak\\diplomka\\apache-tinkerpop-gremlin-console-3.2.4\\conf\\remote.yaml")).create();
    Client  client = cluster.connect();

      //ResultSet result2 = client.submit("g.V(1).repeat(out().simplePath()).until(hasId(5)).path().limit(1)");
    final boolean[] exit = {false};
      Long time = System.currentTimeMillis();
    List<Result> result = client.submit("g.V(1).repeat(out().simplePath()).until(hasId(98677)).path().limit(1)").all().get();
    Long time2 = System.currentTimeMillis();
    System.out.println("it took " + (time2 - time));
    result.stream().forEach(result1 -> System.out.println(result1));
    client.close();
    cluster.close();
/*      CompletableFuture<ResultSet> resultSet = client.submitAsync("g.V(1).repeat(out().simplePath()).until(hasId(98677)).path().limit(1)");
      resultSet.thenAccept(new Consumer<ResultSet>() {
        @Override
        public void accept(ResultSet results) {
          Long time2 = System.currentTimeMillis();
          System.out.println("it took " + (time2 - time));
          results.().forEach(result -> System.out.println(result));
          client.close();
          cluster.close();
          exit[0] = true;
        }
      });
      while (exit[0] != true);*/
  }
  


}