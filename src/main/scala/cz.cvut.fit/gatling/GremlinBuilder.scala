package cz.cvut.fit.gatling

import cz.cvut.fit.gatling.protocol.GremlinProtocol
import cz.cvut.fit.gremlin.core.{GremlinPlainQuery, GremlinQuery, GremlinQueryBuilder}
import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.protocol.ProtocolComponentsRegistry
import io.gatling.core.session.Expression
import io.gatling.core.structure.ScenarioContext

/**
  * Created by cerva on 13/04/2017.
  */


abstract class GremlinBuilder(requestName: Expression[String]) extends ActionBuilder {
  val gremlinQueryBuilder = new GremlinQueryBuilder()

  def getGremlinComponent(protocolComponentsRegistry: ProtocolComponentsRegistry) =
    protocolComponentsRegistry.components(GremlinProtocol.GremlinProtocolKey)

  def createGremlinAction(ctx: ScenarioContext, next: Action, query:GremlinQuery) : Action = {
    import ctx._
    val statsEngine = coreComponents.statsEngine
    val gremlinComponent = getGremlinComponent(protocolComponentsRegistry)
    GremlinAction(requestName, query, gremlinComponent.gremlinProtocol, ctx.system, statsEngine, next)
  }
}


case class GremlinGenericQueryBuilder  (requestName: Expression[String], query: Expression[String]) extends GremlinBuilder (requestName) {
  override def build(ctx: ScenarioContext, next: Action): Action = {
    createGremlinAction(ctx, next, GremlinPlainQuery(query))
  }
}

case class GremlinVertexBuilder  (requestName: Expression[String], id: Expression[String]) extends GremlinBuilder (requestName) {
    override def build(ctx: ScenarioContext, next: Action): Action = {
      val queryTemplate = gremlinQueryBuilder.getVertex(id)
      createGremlinAction(ctx, next, queryTemplate)
    }
}

case class GremlinNeighborsBuilder  (requestName: Expression[String], id: Expression[String], distance: Int) extends GremlinBuilder (requestName) {
  override def build(ctx: ScenarioContext, next: Action): Action = {
    val queryTemplate = gremlinQueryBuilder.neighbors(id, distance)
    createGremlinAction(ctx, next, queryTemplate)
  }
}
