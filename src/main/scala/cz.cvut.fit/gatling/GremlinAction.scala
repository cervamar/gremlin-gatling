package cz.cvut.fit.gatling


import akka.actor.ActorRef
import io.gatling.core.action.{Action, Chainable}
import io.gatling.core.result.message.{KO, OK}
import io.gatling.core.result.writer.DataWriterClient
import io.gatling.core.session.{Expression, Session}
import io.gatling.core.util.TimeHelper

/**
  * Created by cerva on 13/04/2017.
  */
class GremlinAction (requestName: Expression[String], gremlinQuery: Expression[String], protocol: GremlinProtocol, val next: ActorRef) extends Action with Chainable with DataWriterClient{

  override def execute(session: Session): Unit = {
    val resolvedQuery = gremlinQuery.apply(session).get
    val start = TimeHelper.nowMillis
    val result = protocol.call(resolvedQuery)
    val end = TimeHelper.nowMillis
    if (result >= 200 && result <= 299)
      writeRequestData(session, requestName.apply(session).get, start, start, end, end, OK)
    else
      writeRequestData(session, requestName.apply(session).get, start, start, end, end, KO)
    next ! session
  }
}
