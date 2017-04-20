package cz.cvut.fit.gremlin.sources

import org.apache.tinkerpop.gremlin.structure.Graph

/**
  * Created by cerva on 11/04/2017.
  */
trait GraphSource {
    def openGraph(): Graph
}
