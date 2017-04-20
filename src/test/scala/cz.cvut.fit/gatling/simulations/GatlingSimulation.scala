package cz.cvut.fit.gatling.simulations

import cz.cvut.fit.gatling.GremlinPredef.gremlin
import cz.cvut.fit.gatling.protocol.GremlinProtocol
import cz.cvut.fit.gremlin.sources.GenericGraphSource
import io.gatling.core.Predef._

/**
  * Created by cerva on 14/04/2017.
  */
class GatlingSimulation  extends Simulation {

  def loadGraphDefinition(path: String) = {
    new GenericGraphSource(path).openGraph()
  }

  val graph = loadGraphDefinition("src/test/resources/tinkerpop-modern.properties")
  val gremlinProtocol = new GremlinProtocol(graph)

  def scn = scenario("Scenario1").repeat(1){
    exec(gremlin("query").query("g.V(1).repeat(out().simplePath()).until(hasId(5)).path().limit(1)"))
  }

  setUp(
    scn.inject(atOnceUsers(10))
  ).protocols(gremlinProtocol)
}
