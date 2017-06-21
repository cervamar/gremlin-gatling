package cz.cvut.fit.gremlin.core

/**
  * Created on 5/3/2017.
  *
  * @author Marek.Cervak67
  */
trait GremlinQuery {
    def getQuery() : String
    def getVariables(): Map[String, AnyRef]
}

case class GremlinQueryHolder(str: String, map: Map[String, AnyRef]) extends GremlinQuery {
  override def getQuery(): String = str

  override def getVariables(): Map[String, AnyRef] = map
}

class GremlinQueryBuilder() {

  def shortestPath(vertexIdFrom:Object, vertexIdTo:Object) : GremlinQuery = {
    val variables = Map("vertexId" -> vertexIdFrom, "vertexId2" -> vertexIdTo)
    GremlinQueryHolder("g.V(vertexId).repeat(out().simplePath()).until(hasId(vertexId2)).path().limit(1)", variables)
  }
/*
  def addVertex(vertex:Vertex) : GremlinQuery = {
    new GremlinQuery("graph.add")
  }
*/
  def getVertex(vertexId:Object) : GremlinQuery = {
    val variables = Map("vertexId" -> vertexId)
    new GremlinQueryHolder("g.V(vertexId)", variables);
  }

  def query(query:String) : GremlinQuery = {
    new GremlinQueryHolder(query, Map.empty)
  }

  def neighbors(vertexIdFrom:Object, distance:Int) : GremlinQuery = {
    val variables = Map("vertexId" -> vertexIdFrom, "distance" -> Int.box(distance))
    GremlinQueryHolder("g.V(vertexId).repeat(out()).times(distance).simplePath()", variables)
  }


}



