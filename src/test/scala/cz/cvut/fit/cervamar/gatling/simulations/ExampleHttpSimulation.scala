package cz.cvut.fit.cervamar.gatling.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder.toActionBuilder

/**
  * Example Gatling load test that sends two HTTP requests to the same URL.
  */
class ExampleHttpSimulation extends Simulation {

  val httpProtocol = http

  val scn = scenario("Bežný uživatel")
    .exec(http("Otevři stránku vyhledávače")
      .get("https://www.google.cz"))
    .pause(3)
    .exec(http("Najdi restaurace na Praze 6")
      .get("https://www.google.cz/#q=restaurace+praha+6").check(status.is(200)).check())
    .pause(2)

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(
    global.responseTime.max.lt(100),
    global.successfulRequests.percent.gt(95)
  )
}