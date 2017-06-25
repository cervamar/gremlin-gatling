package cz.cvut.fit.gatling.protocol

import akka.actor.ActorSystem
import io.gatling.core.CoreComponents
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.protocol.{Protocol, ProtocolKey}

/**
  * Created by cerva on 13/04/2017.
  */

object GremlinProtocol {

  val GremlinProtocolKey = new ProtocolKey {

    type Protocol = GremlinProtocol
    type Components = GremlinComponents

    def protocolClass: Class[io.gatling.core.protocol.Protocol] = classOf[GremlinProtocol].asInstanceOf[Class[io.gatling.core.protocol.Protocol]]

    def defaultProtocolValue(configuration: GatlingConfiguration): GremlinProtocol = throw new IllegalStateException("Can't provide a default value for GremlinProtocol")

    def newComponents(system: ActorSystem, coreComponents: CoreComponents): GremlinProtocol => GremlinComponents = {
      gremlinProtocol: GremlinProtocol => GremlinComponents(gremlinProtocol)
    }

  }
}

case class GremlinProtocol (serverClient : GremlinServerClient) extends Protocol {
  type Components = GremlinComponents
  val utility = new Utility
  val supportNumericIds : Boolean = serverClient.getClient.submit("graph.features().vertex().supportsNumericIds()").all.get.get(0).getBoolean
}
