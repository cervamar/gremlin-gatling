package cz.cvut.fit.gremlin.sources
import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph

/**
  * Created by cerva on 11/04/2017.
  */
object TinkergraphSource extends GraphSource{
  override def openGraph(): Graph = TinkerGraph.open
}
