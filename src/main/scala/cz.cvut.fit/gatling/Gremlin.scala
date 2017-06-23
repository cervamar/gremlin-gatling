package cz.cvut.fit.gatling

import io.gatling.core.session.Expression

/**
  * Created on 4/18/2017.
  *
  * @author Marek.Cervak
  */
case class Gremlin(requestName: Expression[String]) {
     def query(query: Expression[String]) = new GremlinGenericQueryBuilder(requestName, query)
     def getVertex(id: Expression[String]) = new GremlinVertexBuilder(requestName, id)
     def getNeighbours(id: Expression[String], distance:Int) = new GremlinNeighborsBuilder(requestName, id, distance)
}
