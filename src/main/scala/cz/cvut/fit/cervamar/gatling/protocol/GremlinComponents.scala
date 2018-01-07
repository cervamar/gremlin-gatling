package cz.cvut.fit.cervamar.gatling.protocol

import io.gatling.core.protocol.ProtocolComponents
import io.gatling.core.session.Session

case class GremlinComponents(gremlinProtocol: GremlinProtocol) extends ProtocolComponents {
  override def onStart: Option[(Session) => Session] = Option((session) => {gremlinProtocol.openClient()
    session})
  override def onExit: Option[(Session) => Unit] = Option((session) => gremlinProtocol.closeClient())
}
