package cz.cvut.fit.gatling.simulations

import cz.cvut.fit.gatling.GremlinPredef.gremlin
import cz.cvut.fit.gatling.protocol.{GremlinProtocol, GremlinServerClient}
import io.gatling.core.Predef._

/**
  * Created by cerva on 14/04/2017.
  */
class TestConnectionSimulation  extends Simulation {

  val gremlinProtocol = new GremlinProtocol(GremlinServerClient.createGremlinServerClient("src/main/resources/remote.yaml"))

  def scn = scenario("User")
        .exec(gremlin("countVertices").query("g.V().count()"))

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(gremlinProtocol)

}
