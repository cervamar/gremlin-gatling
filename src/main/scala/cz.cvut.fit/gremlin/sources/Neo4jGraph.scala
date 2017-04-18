package cz.cvut.fit.gremlin.sources

import org.apache.tinkerpop.gremlin.neo4j.structure.Neo4jGraph
import org.apache.tinkerpop.gremlin.structure.Graph

/**
  * Created by cerva on 11/04/2017.
  */
object Neo4jGraphResource extends GraphSource{

  override def getEmptyInstance(): Graph = Neo4jGraph.open("/tmp/neo4j")
}
