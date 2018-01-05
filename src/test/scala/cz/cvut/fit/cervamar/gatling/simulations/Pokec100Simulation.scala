package cz.cvut.fit.cervamar.gatling.simulations

import cz.cvut.fit.cervamar.gatling.GremlinPredef.{gremlin, simpleCheck}
import cz.cvut.fit.cervamar.gatling.protocol.GremlinProtocol
import io.gatling.core.Predef._
import org.apache.tinkerpop.gremlin.driver.Result

/**
  * Created on 1/2/2018.
  *
  * @author Marek.Cervak
  */
class Pokec100Simulation extends Simulation {

  val gremlinProtocol = new GremlinProtocol("src/main/resources/remote.yaml")

  val idFeeder = Iterator.continually(Map("id" -> "1", "id2" -> "2"))

  def scn = scenario("User")
    .feed(idFeeder)
    .exec(gremlin("getProfile")
      .query("g.V().has('pokecId','1').next()")
      .extractResultAndSaveAs(parseVertexId, "profileId"))
    .exec{session =>
      println(session("profileId").as[String])
      session}
    .exec(gremlin("getUserFriends")
      .neighbours("${profileId}")
      .check(simpleCheck(res => res.size == 13))
      .extractResultAndSaveAs(parseVertexId, "neighbourId"))
    .exec{session =>
      println(session("neighbourId").as[String])
      session}
  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(gremlinProtocol)

  def parseVertexId(result : List[Result]) : String = {
    val vertex = result.get.head.getString
    println(vertex)
    val pattern = "v\\[(\\w)\\]".r
    vertex match {
      case pattern(id) => id
      case _ => parseOrientVertexId(result)
    }
  }

  def parseOrientVertexId(result : List[Result]) : String = {
    val vertex = result.get.head.getString
    val pattern = "v\\[#(\\d+:\\d+)\\]".r
    vertex match {
      case pattern(id) => id
      case _ => throw new MatchError()
    }
  }
}
