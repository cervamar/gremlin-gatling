package cz.cvut.fit.cervamar.gremlin.core

import java.util

import com.typesafe.scalalogging.StrictLogging
import cz.cvut.fit.cervamar.gatling.util.ScalaUtils
import io.gatling.core.session.{Expression, Session, StaticStringExpression}

/**
  * Created on 5/3/2017.
  *
  * @author Marek.Cervak67
  */

trait GremlinQuery extends StrictLogging{
    def getPlainQuery(session: Session): String = {
      getQuery.apply(session).get
    }
    def getQuery: Expression[String]
    def getExpressionVariables: Map[String, Expression[String]] = Map.empty
    def getVariables: Map[String, Object] = Map.empty
    def getAllVariables(session: Session, numericId : Boolean): Map[String, Object] = {
      var ret = getVariables
      getExpressionVariables.foreach((m) => {
        val res = m._2.apply(session).get
        if(m._1.startsWith(GremlinQuery.id_prefix) && !numericId) {
          ret += (m._1 -> ("\"" + res + "\""))
        }
        else {
          ret += (m._1 -> res)
        }
      })
      ret
    }

  def getResolvedQuery(session: Session, hasProtocolNumericIds: Boolean) : ResolvedQuery = {
    var ret = getVariables
    if(hasProtocolNumericIds) {
      getExpressionVariables.foreach((m) => {
        val res = m._2.apply(session).get
        ret += (m._1 -> res)
    })
      ResolvedQuery(getPlainQuery(session), ScalaUtils.createJavaMap(ret))
    }
    else {
      var query = getPlainQuery(session)
      logger.debug("query: {}, map:", query)
      getExpressionVariables.foreach((m) => {
        val res = m._2.apply(session).get
        logger.debug("(k,v): {},{}, map:", m._1, res)
        if(m._1.startsWith(GremlinQuery.id_prefix)) {
          query = query.replaceFirst(m._1, "\"" + res + "\"")
        }
        else ret += (m._1 -> res)
      })
      logger.debug("query after: {}", query)
      ResolvedQuery(query, ScalaUtils.createJavaMap(ret))
    }
  }


}

case class ResolvedQuery (query:String, bindings: util.Map[String,Object])

object GremlinQuery {
  val bind_prefix = "bind"
  val id_prefix = bind_prefix + "Id"
}

case class GremlinQueryHolder(str: Expression[String],
                              expressionVariables: Map[String, Expression[String]] = Map.empty,
                              variables: Map[String, AnyRef] = Map.empty) extends GremlinQuery {
  override def getQuery : Expression[String] = str

  override def getVariables: Map[String, AnyRef] = variables

  override def getExpressionVariables: Map[String, Expression[String]] = expressionVariables

}

case class GremlinPlainQuery(str: Expression[String],
                             bindings: Map[String, AnyRef]) extends GremlinQuery {
  override def getQuery: Expression[String] = str
  override def getVariables: Map[String, Object] = bindings
}

class GremlinQueryBuilder() {

  def shortestPath(vertexIdFrom: String, vertexIdTo: String) : GremlinQuery = {
    shortestPath(toExpression(vertexIdFrom), toExpression(vertexIdTo))
  }
  def shortestPath(vertexIdFrom: Expression[String], vertexIdTo: Expression[String]) : GremlinQuery = {
    val id1 = GremlinQuery.id_prefix + 1
    val id2 = GremlinQuery.id_prefix + 2
    val expressionVariables = Map( id1 -> vertexIdFrom, id2 -> vertexIdTo)
    GremlinQueryHolder(toExpression("g.V( + " + id1 + ").repeat(out().simplePath()).until(hasId("
        + id2 + ").path().limit(1)"), expressionVariables)
  }
  def getVertex(vertexId: String) : GremlinQuery = {
    getVertex(toExpression(vertexId))
  }

  def getVertex(vertexId: Expression[String]) : GremlinQuery = {
    val id1 = GremlinQuery.id_prefix + 1
    GremlinQueryHolder(toExpression("g.V(" + id1 +")"), Map(id1 -> vertexId))
  }

  def getVertexByProperty(property: Expression[String], value: Expression[String]) : GremlinQuery = {
    val prop = GremlinQuery.bind_prefix + "prop"
    val valueKey = GremlinQuery.bind_prefix + "value"
    GremlinQueryHolder(toExpression("g.V().has(" + prop + "," +  valueKey + ").next()"),
      Map(prop -> property, valueKey -> value))
  }

  def query(query: String) : GremlinQuery = {
    GremlinQueryHolder(toExpression(query))
  }

  def query(query: Expression[String]) : GremlinQuery = {
    GremlinQueryHolder(query)
  }

  def neighbors(vertexIdFrom: String): GremlinQuery = {
    neighbors(toExpression(vertexIdFrom), 1)
  }

  def neighbors(vertexIdFrom: String, distance:Int): GremlinQuery = {
    neighbors(toExpression(vertexIdFrom), distance)
  }

  def neighbors(vertexIdFrom: Expression[String], distance:Int) : GremlinQuery = {
    val id1 = GremlinQuery.id_prefix + 1
    val dist = GremlinQuery.bind_prefix + "dist"
    GremlinQueryHolder(toExpression("g.V(" + id1+").repeat(out()).times(" + dist + ").simplePath()"),
      Map(id1 -> vertexIdFrom), Map(dist -> Int.box(distance)))
  }

  def mutualNeighbors(vertexId: String, vertexId2: String): GremlinQuery = {
    mutualNeighbors(toExpression(vertexId), toExpression(vertexId2))
  }

  def mutualNeighbors(vertexId: Expression[String], vertexId2: Expression[String]) : GremlinQuery = {
    val id1 = GremlinQuery.id_prefix + 1
    val id2 = GremlinQuery.id_prefix + 2
    val expressionVariables = Map(id1 -> vertexId, id2 -> vertexId2)
    GremlinQueryHolder(toExpression("g.V(" + id1 + ").out()" + ".as('x').in().hasId(" + id2 + ").select('x')"), expressionVariables)
  }

  def toExpression(string : String): Expression[String] = {
    StaticStringExpression(string)
  }
}



