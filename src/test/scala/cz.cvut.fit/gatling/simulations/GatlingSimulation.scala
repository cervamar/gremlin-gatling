package cz.cvut.fit.gatling.simulations

import cz.cvut.fit.gatling.GremlinPredef.gremlin
import cz.cvut.fit.gatling.protocol.{GremlinClient, GremlinProtocol}
import io.gatling.core.Predef._

/**
  * Created by cerva on 14/04/2017.
  */
class GatlingSimulation  extends Simulation {

  val gremlinProtocol = new GremlinProtocol(new GremlinClient().getClient)

  val r = scala.util.Random
  val feeder = Iterator.continually(Map("id" -> ("\"" + (r.nextInt(13) + "\""))))
  val feederAll = Iterator.continually(Map("id" -> ("\"" + (r.nextInt(100000) + "\""))))

  def scn = scenario("Scenario1").repeat(2){
    //exec(gremlin("query").query("g.V(\"1\").repeat(out().simplePath()).until(hasId(\"5\")).path().limit(1)"))
    feed(feederAll).exec(gremlin("getProfile").getVertex("${id}")).
    feed(feederAll).exec(gremlin("getProfile").getNeighbours("${id}", 1))
    //feed(feeder).exec(gremlin("query").query("g.V(${id}).repeat(out().simplePath()).until(hasId(98677)).path().limit(1)"))
    //exec(gremlin("query").query("g.V().has(\"name\", \"marko\").repeat(out().simplePath()).until(has(\"name\", \"ripple\")).path().limit(1)"))

  }

  setUp(
    scn.inject(rampUsers(100) over 20)
  ).protocols(gremlinProtocol)

}
