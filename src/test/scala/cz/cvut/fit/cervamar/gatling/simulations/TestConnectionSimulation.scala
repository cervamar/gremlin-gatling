package cz.cvut.fit.cervamar.gatling.simulations

import cz.cvut.fit.cervamar.gatling.GremlinPredef._
import cz.cvut.fit.cervamar.gatling.protocol.GremlinProtocol
import io.gatling.core.Predef._

/**
  * Created by cerva on 14/04/2017.
  */
class TestConnectionSimulation  extends Simulation {

  val gremlinProtocol = new GremlinProtocol("src/main/resources/remote.yaml")

  def scn = scenario("User")
        .exec(gremlin("countVertices")
          .query("g.V().count()")
          .check(simpleCheck(result => result.size == 1))
          .check(simpleCheck(result => result.head.getLong == 6)))
    .exec(gremlin("countVertices2")
      .query("g.V(4)")
      .check(simpleCheck(result => result.size == 1))
      .extractResultAndSaveAs(result => result.head.getString, "vertex"))
       .exec{session =>
          println(session("vertex").as[String])
      session}
  setUp(
    scn.inject(atOnceUsers(2))
  ).protocols(gremlinProtocol)

}