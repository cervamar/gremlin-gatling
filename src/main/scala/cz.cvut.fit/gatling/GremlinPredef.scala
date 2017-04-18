package cz.cvut.fit.gatling

import io.gatling.core.session.Expression

/**
  * Created on 4/18/2017.
  *
  * @author Marek.Cervak
  */
object GremlinPredef {
  def gremlin(requestName: Expression[String]) = Gremlin(requestName)
}
