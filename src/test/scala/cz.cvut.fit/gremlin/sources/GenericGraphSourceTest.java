package cz.cvut.fit.gremlin.sources;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.junit.Test;

/**
  * Created by cerva on 11/04/2017.
  */

public class GenericGraphSourceTest {

  @Test
  public void insertVector() {
    Graph graph = new GenericGraphSource("src/test/resources/neo4j-standalone.properties").getEmptyInstance();
    String label = "france";
    graph.addVertex(label);
/*     paris = graph + label
    graph + "germany"
    val vertices = graph.V()
    assert(graph.V(paris.id()).exists())
    val labeledVertices = graph.V.hasLabel(label)
    println(labeledVertices.count.head())
    println("paris has id" + paris.id())
    println(graph.V.value("id").toList)
    graph.tx().commit()
    graph.close()*/
  }

}
