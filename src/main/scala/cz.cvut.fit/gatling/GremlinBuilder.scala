package cz.cvut.fit.gatling

import akka.actor.ActorDSL._
import akka.actor.ActorRef
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.config.Protocols
import io.gatling.core.session.Expression

/**
  * Created by cerva on 13/04/2017.
  */
case class GremlinBuilder  (requestName: Expression[String], query: Expression[String]) extends ActionBuilder {

  def gremlinProtocol(protocols: Protocols) =
    protocols.getProtocol[GremlinProtocol]
      .getOrElse(throw new UnsupportedOperationException("GremlinProtocol Protocol wasn't registered"))

  override def build(next: ActorRef, protocols: Protocols): ActorRef = {
    actor(actorName("Functioncall")) {
      new GremlinAction(requestName, query, gremlinProtocol(protocols), next)
    }
  }
}
