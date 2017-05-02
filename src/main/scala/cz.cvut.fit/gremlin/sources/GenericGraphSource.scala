package cz.cvut.fit.gremlin.sources

import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory

/**
  * Created by cerva on 13/04/2017.
  */
class GenericGraphSource(configPath: String) extends GraphSource{

  override def openGraph(): Graph = GraphFactory.open(configPath)

}
