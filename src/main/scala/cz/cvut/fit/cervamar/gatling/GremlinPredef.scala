package cz.cvut.fit.cervamar.gatling

import cz.cvut.fit.cervamar.gatling.check.ResultCheckSupport
import io.gatling.core.session.Expression

/**
  * Created on 4/18/2017.
  *
  * @author Marek.Cervak
  */
object GremlinPredef extends ResultCheckSupport {
  def gremlin(requestName: Expression[String]) = Gremlin(requestName)
}
