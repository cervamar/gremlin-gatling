package cz.cvut.fit.cervamar.gatling.simulations

import cz.cvut.fit.cervamar.gatling.GremlinPredef.{gremlin, simpleCheck}
import cz.cvut.fit.cervamar.gatling.protocol.GremlinProtocol
import io.gatling.core.Predef._

/**
  * Created by cerva on 14/04/2017.
  */
class FinalSimulation  extends Simulation {

  val gremlinProtocol = new GremlinProtocol("src/main/resources/remote.yaml")

  val r = scala.util.Random
  val idFeeder = Iterator.continually(Map("id" -> ("\"" + (r.nextInt(100000) + "\"")), "id2" -> ("\"" + (r.nextInt(100000) + "\""))))

  def scn = scenario("User")
    .feed(idFeeder).exec(gremlin("getProfile").vertex("${id}"))
    .exec(gremlin("getUserFriends").neighbours("${id}", 1).check(simpleCheck(result => result.size == 1)))
    .exec(gremlin("getMutualFriends").mutualNeigbours("${id}", "${id2}"))

  setUp(
    scn.inject(rampUsers(2000) over 20)
  ).protocols(gremlinProtocol)

}
