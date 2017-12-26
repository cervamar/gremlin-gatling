package cz.cvut.fit.cervamar.gatling.simulations

import cz.cvut.fit.cervamar.gatling.GremlinPredef.gremlin
import cz.cvut.fit.cervamar.gatling.protocol.{GremlinProtocol, GremlinServerClient}
import io.gatling.core.Predef._

/**
  * Created by cerva on 14/04/2017.
  */
class GatlingSimulation  extends Simulation {

  val gremlinProtocol = new GremlinProtocol(GremlinServerClient.createGremlinServerClient("src/main/resources/remote.yaml"))

  val r = scala.util.Random
  val idFeeder = Iterator.continually(Map("id" -> ("\"" + (r.nextInt(100000) + "\"")), "id2" -> ("\"" + (r.nextInt(100000) + "\""))))

  def scn = scenario("User")
      .feed(idFeeder).exec(gremlin("getProfile").vertex("${id}"))
      .exec(gremlin("getUserFriends").neighbours("${id}", 1))
      .exec(gremlin("getMutualFriends").mutualNeigbours("${id}", "${id2}"))

  setUp(
    scn.inject(rampUsers(3000) over 20)
  ).protocols(gremlinProtocol)

}
