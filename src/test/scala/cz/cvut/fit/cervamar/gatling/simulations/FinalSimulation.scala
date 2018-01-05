package cz.cvut.fit.cervamar.gatling.simulations

import cz.cvut.fit.cervamar.gatling.GremlinPredef._
import cz.cvut.fit.cervamar.gatling.protocol.GremlinProtocol
import io.gatling.core.Predef._
import org.apache.tinkerpop.gremlin.driver.Result

/**
  * Created by cerva on 14/04/2017.
  */
class FinalSimulation  extends Simulation {

  val r = scala.util.Random
  val idFeeder = Iterator.continually(Map("pokecId" -> r.nextInt(10000) ))

  val gremlinProtocol = new GremlinProtocol("src/main/resources/remote.yaml")

  def scn = scenario("Load test")
    .feed(idFeeder)
    .exec(gremlin("getProfile")
      .vertexByProperty("pokecId", "${pokecId}")
      .check(simpleCheck(res => res.size == 1))
      .extractResultAndSaveAs(parseVertexId, "profileId"))
    .exec(gremlin("getUserFriends")
      .neighbours("${profileId}")
      .extractResultAndSaveAs(parseVertexId, "neighbourId"))
    .doIf("${neighbourId.exists()}") {
      exec(gremlin("getFriendProfile")
        .vertex("${neighbourId}")
        .check(simpleCheck(res => res.size == 1)))
      .exec(gremlin("getMutualFriends")
        .mutualNeigbours("${profileId}", "${neighbourId}"))
    }

  setUp(scn.inject(constantUsersPerSec(1) during 1))
    .protocols(gremlinProtocol)
    .assertions(
      global.failedRequests.count.is(0),
      details("getProfile").responseTime.percentile3.lt(150),
      global.responseTime.max.lt(1000)
    )

  def parseVertexId(result : List[Result]) : String = {
    val vertex = result.head.getString
    val pattern = "v\\[(\\w+)\\]".r
    vertex match {
      case pattern(id) => id
      case _ => parseOrientVertexId(result)
    }
  }

  def parseOrientVertexId(result : List[Result]) : String = {
    val vertex = result.head.getString
    val pattern = "v\\[#(\\d+:\\d+)\\]".r
    vertex match {
      case pattern(id) => id
      case _ => throw new MatchError()
    }
  }

}
