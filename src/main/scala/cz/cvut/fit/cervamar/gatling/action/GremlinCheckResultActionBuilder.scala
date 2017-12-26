package cz.cvut.fit.cervamar.gatling.action

import cz.cvut.fit.cervamar.gatling.ResultCheck
import io.gatling.core.action.builder.ActionBuilder

import scala.collection.mutable.ArrayBuffer

/**
  * Created on 12/26/2017.
  *
  * @author Marek.Cervak
  */
trait GremlinCheckResultActionBuilder extends ActionBuilder {

    protected val checks: ArrayBuffer[ResultCheck] = ArrayBuffer.empty

    def check(check: ResultCheck): ActionBuilder = {
      checks += check
      this
    }

}
