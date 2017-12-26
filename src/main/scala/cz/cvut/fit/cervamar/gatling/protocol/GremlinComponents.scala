package cz.cvut.fit.cervamar.gatling.protocol

import io.gatling.core.protocol.ProtocolComponents
import io.gatling.core.session.Session

case class GremlinComponents(gremlinProtocol: GremlinProtocol) extends ProtocolComponents {
  override def onStart: Option[(Session) => Session] = None
  override def onExit: Option[(Session) => Unit] = None
}
