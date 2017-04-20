package cz.cvut.fit.gatling.protocol

import io.gatling.core.protocol.ProtocolComponents
import io.gatling.core.session.Session

case class GremlinComponents(gremlinProtocol: GremlinProtocol) extends ProtocolComponents {

  def onStart: Option[Session => Session] = None
  def onExit: Option[Session => Unit] = None
}
