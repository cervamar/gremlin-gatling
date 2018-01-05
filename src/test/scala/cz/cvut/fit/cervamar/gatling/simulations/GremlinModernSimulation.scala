package cz.cvut.fit.cervamar.gatling.simulations

import cz.cvut.fit.cervamar.gatling.GremlinPredef._
import cz.cvut.fit.cervamar.gatling.protocol.GremlinProtocol
import io.gatling.core.Predef._
import org.apache.tinkerpop.gremlin.driver.Result

/**
  * Created by cerva on 14/04/2017.
  */
class GremlinModernSimulation  extends Simulation {

  val gremlinProtocol = new GremlinProtocol("src/main/resources/remote.yaml")

  def scn = scenario("User")
    .exec(gremlin("getProfile")
      .vertexByProperty("name", "marko")
      .extractResultAndSaveAs(parseVertexId, "profileId"))
    .exec(gremlin("getUserFriends")
      .neighbours("${profileId}", 1)
      .check(simpleCheck(res => res.size == 3)))
    .exec(gremlin("getMutualFriends")
      .mutualNeigbours("${profileId}", "4")
      .check(simpleCheck(res => res.size == 1)))
    .exec(gremlin("getMutualFriends2")
      .mutualNeigbours("${profileId}", "5")
      .check(simpleCheck(res => res.size == 0)))

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(gremlinProtocol)

  def parseVertexId(result : List[Result]) : String = {
      val vertex = result.get.head.getString
      val pattern = "v\\[(\\w)\\]".r
    vertex match {
      case pattern(id) => id
      case _ => throw new MatchError()
    }
  }
}
