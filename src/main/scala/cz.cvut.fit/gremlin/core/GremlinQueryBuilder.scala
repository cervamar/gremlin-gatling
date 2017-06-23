package cz.cvut.fit.gremlin.core

import io.gatling.core.session.{Expression, Session, StaticStringExpression}

import scala.collection.mutable.ArrayBuffer

/**
  * Created on 5/3/2017.
  *
  * @author Marek.Cervak67
  */
trait GremlinQuery {
    def getPlainQuery(session: Session, numericId : Boolean): String = {
      val stringVariables = getStringVariables
      val seq = ArrayBuffer[String]()
      getStringVariables.foreach((m) => {
        val res = m._2.apply(session).get
        if(m._1.startsWith(GremlinQuery.id_prefix) && numericId) {
            seq += res
        }
        else {
            seq += ("\"" + res + "\"")
        }
      })
      getQuery.apply(session).get.format(seq:_*)
    }
    def getQuery: Expression[String]
    def getStringVariables: Map[String, Expression[String]] = Map.empty
    def getVariables: Map[String, Object] = Map.empty
    def getAllVariables(session: Session): Map[String, Object] = Map.empty

}

object GremlinQuery {
  val id_prefix = "id";
}

case class GremlinQueryHolder(str: Expression[String], stringVariables: Map[String, Expression[String]], variables: Map[String, AnyRef]) extends GremlinQuery {
  override def getQuery : Expression[String] = str

  override def getVariables: Map[String, AnyRef] = variables

  override def getStringVariables: Map[String, Expression[String]] = stringVariables

  override def getAllVariables(session: Session): Map[String, Object] = {
    val result = scala.collection.mutable.Map[String, Object]()
    variables.foreach((m) => result.put(m._1, m._2))
    //stringVariables.foreach((m) => result.put(m._1, m._2.apply(session).get))
    result.toMap
  }
}

case class GremlinPlainQuery(str: Expression[String]) extends GremlinQuery {
  override def getQuery: Expression[String] = str
}

class GremlinQueryBuilder() {

  def shortestPath(vertexIdFrom: String, vertexIdTo: String) : GremlinQuery = {
    shortestPath(StaticStringExpression(vertexIdFrom), StaticStringExpression(vertexIdTo))
  }
  def shortestPath(vertexIdFrom: Expression[String], vertexIdTo: Expression[String]) : GremlinQuery = {
    val stringVariables = Map(GremlinQuery.id_prefix + 1 -> vertexIdFrom, GremlinQuery.id_prefix + 2 -> vertexIdTo)
    GremlinQueryHolder(StaticStringExpression("g.V(%s).repeat(out().simplePath()).until(hasId(%s)).path().limit(1)"), stringVariables, Map.empty)
  }
  def getVertex(vertexId: String) : GremlinQuery = {
    getVertex(StaticStringExpression(vertexId))
  }

  def getVertex(vertexId: Expression[String]) : GremlinQuery = {
    val variables = Map(GremlinQuery.id_prefix -> vertexId)
    GremlinQueryHolder(StaticStringExpression("g.V(%s)"), variables, Map.empty)
  }

  def query(query: String) : GremlinQuery = {
    GremlinQueryHolder(StaticStringExpression(query), Map.empty, Map.empty)
  }

  def query(query: Expression[String]) : GremlinQuery = {
    GremlinQueryHolder(query, Map.empty, Map.empty)
  }

  def neighbors(vertexIdFrom: String, distance:Int): GremlinQuery = {
    neighbors(StaticStringExpression(vertexIdFrom), distance)
  }

  def neighbors(vertexIdFrom: Expression[String], distance:Int) : GremlinQuery = {
    val stringVariables = Map(GremlinQuery.id_prefix -> vertexIdFrom)
    val variables = Map("distance" -> Int.box(distance))
    GremlinQueryHolder(StaticStringExpression("g.V(%s).repeat(out()).times(distance).simplePath()"), stringVariables, variables)
  }

}



