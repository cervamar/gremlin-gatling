package cz.cvut.fit.cervamar.gatling.action

import cz.cvut.fit.cervamar.gatling.protocol.GremlinProtocol
import cz.cvut.fit.cervamar.gremlin.core.{GremlinPlainQuery, GremlinQuery, GremlinQueryBuilder}
import io.gatling.core.action.Action
import io.gatling.core.protocol.ProtocolComponentsRegistry
import io.gatling.core.session.Expression
import io.gatling.core.structure.ScenarioContext

/**
  * Created by cerva on 13/04/2017.
  */


abstract class GremlinBuilder(requestName: Expression[String]) extends GremlinCheckResultActionBuilder {
  protected val gremlinQueryBuilder = new GremlinQueryBuilder()

  def getGremlinComponent(protocolComponentsRegistry: ProtocolComponentsRegistry) =
    protocolComponentsRegistry.components(GremlinProtocol.GremlinProtocolKey)

  def createGremlinAction(ctx: ScenarioContext, next: Action, query:GremlinQuery) : Action = {
    import ctx._
    val statsEngine = coreComponents.statsEngine
    val gremlinComponent = getGremlinComponent(protocolComponentsRegistry)
    GremlinExecuteAction(requestName, query, checks.toList, extract, gremlinComponent.gremlinProtocol, statsEngine, next)
  }
}

case class GremlinGenericQueryBuilder  (requestName: Expression[String],
                                        query: Expression[String],
                                        bindings: Map[String, AnyRef] = Map.empty) extends GremlinBuilder (requestName) {
  override def build(ctx: ScenarioContext, next: Action): Action = {
    createGremlinAction(ctx, next, GremlinPlainQuery(query, bindings))
  }
}

case class GremlinVertexBuilder(requestName: Expression[String], id: Expression[String]) extends GremlinBuilder (requestName) {
    override def build(ctx: ScenarioContext, next: Action): Action = {
      val queryTemplate = gremlinQueryBuilder.getVertex(id)
      createGremlinAction(ctx, next, queryTemplate)
    }
}

case class GremlinVertexByPropertyBuilder(requestName: Expression[String], property: Expression[String], value: Expression[String]) extends GremlinBuilder(requestName) {
  override def build(ctx: ScenarioContext, next: Action): Action = {
    val queryTemplate = gremlinQueryBuilder.getVertexByProperty(property, value)
    createGremlinAction(ctx, next, queryTemplate)
  }
}

case class GremlinNeighborsBuilder (requestName: Expression[String], id: Expression[String], distance: Int = 1) extends GremlinBuilder (requestName) {
  override def build(ctx: ScenarioContext, next: Action): Action = {
    val queryTemplate = gremlinQueryBuilder.neighbors(id, distance)
    createGremlinAction(ctx, next, queryTemplate)
  }
}

case class GremlinMutualBuilder (requestName: Expression[String], id: Expression[String], id2: Expression[String]) extends GremlinBuilder (requestName) {

  override def build(ctx: ScenarioContext, next: Action): Action = {
    val queryTemplate = gremlinQueryBuilder.mutualNeighbors(id, id2)
    createGremlinAction(ctx, next, queryTemplate)
  }
}
