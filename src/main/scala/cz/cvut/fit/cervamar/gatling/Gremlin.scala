package cz.cvut.fit.cervamar.gatling

import cz.cvut.fit.cervamar.gatling.action._
import io.gatling.core.session.Expression

/**
  * Created on 4/18/2017.
  *
  * @author Marek.Cervak
  */
case class Gremlin(requestName: Expression[String]) {
     def query(query: Expression[String]) = GremlinGenericQueryBuilder(requestName, query)
     def vertex(id: Expression[String]) = GremlinVertexBuilder(requestName, id)
     def vertexByProperty(property: Expression[String], value: Expression[String]) = GremlinVertexByPropertyBuilder(requestName, property, value);
     def neighbours(id: Expression[String], distance:Int) = GremlinNeighborsBuilder(requestName, id, distance)
     def mutualNeigbours(id: Expression[String], id2: Expression[String]) = GremlinMutualBuilder(requestName, id, id2)
}
