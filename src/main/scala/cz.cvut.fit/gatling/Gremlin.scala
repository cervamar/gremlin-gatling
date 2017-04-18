package cz.cvut.fit.gatling

import io.gatling.core.session.Expression

/**
  * Created on 4/18/2017.
  *
  * @author Marek.Cervak
  */
case class Gremlin(requestName: Expression[String]) {
     def query(query: Expression[String]) = new GremlinBuilder(requestName, query)
}
