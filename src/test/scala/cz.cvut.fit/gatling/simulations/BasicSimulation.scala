package cz.cvut.fit.gatling.simulations

import cz.cvut.fit.gatling.GremlinPredef.gremlin
import cz.cvut.fit.gatling.protocol.{GremlinClient, GremlinProtocol}
import io.gatling.core.Predef._

/**
  * Created by cerva on 14/04/2017.
  */
class BasicSimulation  extends Simulation {

  val gremlinProtocol = new GremlinProtocol(new GremlinClient().getClient)

  def scn = scenario("Scenario1").repeat(5){
    //exec(gremlin("query").query("g.V(\"1\").repeat(out().simplePath()).until(hasId(\"5\")).path().limit(1)"))
    //exec(gremlin("query").query("g.V().has(\"name\", \"marko\").repeat(out().simplePath()).until(has(\"name\", \"ripple\")).path().limit(1)"))
    exec(gremlin("query").query("g.V(\"1\").repeat(out().simplePath()).until(hasId(\"98677\")).path().limit(1)"))
  }

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(gremlinProtocol)

  //graph.close()
}