package cz.cvut.fit.gremlin.core

import javax.script.Bindings

import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine

/**
  * Created on 5/3/2017.
  *
  * @author Marek.Cervak
  */

trait EvaluableScript {
  def eval() : Object
}
class EvaluableScriptQuery(query: String, bindings: Bindings, engine:GremlinGroovyScriptEngine) extends EvaluableScript {
  override def eval(): AnyRef = {
      engine.eval(query, bindings)
  }
}
