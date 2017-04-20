package cz.cvut.fit.gatling

import cz.cvut.fit.gatling.protocol.GremlinProtocol
import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.protocol.ProtocolComponentsRegistry
import io.gatling.core.session.Expression
import io.gatling.core.structure.ScenarioContext

/**
  * Created by cerva on 13/04/2017.
  */
case class GremlinBuilder  (requestName: Expression[String], query: Expression[String]) extends ActionBuilder {

  def getGremlinComponent(protocolComponentsRegistry: ProtocolComponentsRegistry) =
    protocolComponentsRegistry.components(GremlinProtocol.GremlinProtocolKey)
      //.getOrElse(throw new UnsupportedOperationException("GremlinProtocol Protocol wasn't registered"))

  override def build(ctx: ScenarioContext, next: Action): Action = {
    import ctx._
    val statsEngine = coreComponents.statsEngine
    val gremlinComponent = getGremlinComponent(protocolComponentsRegistry)
      GremlinAction(requestName, query, gremlinComponent.gremlinProtocol, ctx.system, statsEngine, next)
  }
}
