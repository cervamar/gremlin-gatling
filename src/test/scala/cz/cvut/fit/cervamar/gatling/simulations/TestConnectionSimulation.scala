package cz.cvut.fit.cervamar.gatling.simulations

import cz.cvut.fit.cervamar.gatling.GremlinPredef._
import cz.cvut.fit.cervamar.gatling.protocol.{GremlinProtocol, GremlinServerClient}
import io.gatling.core.Predef._

/**
  * Created by cerva on 14/04/2017.
  */
class TestConnectionSimulation  extends Simulation {

  val gremlinProtocol = new GremlinProtocol(GremlinServerClient.createGremlinServerClient("src/main/resources/remote.yaml"))

  def scn = scenario("User")
        .exec(gremlin("countVertices")
          .query("g.V().count()")
          .check(simpleCheck(result => result.size == 1)))
    .exec(gremlin("countVertices2")
      .query("g.V().count()")
      .check(simpleCheck(result => result.isEmpty)))
  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(gremlinProtocol)

}
