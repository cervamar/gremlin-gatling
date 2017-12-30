package cz.cvut.fit.cervamar.gatling.action

import cz.cvut.fit.cervamar.gatling.ResultCheck
import io.gatling.core.action.builder.ActionBuilder
import org.apache.tinkerpop.gremlin.driver.Result

import scala.collection.mutable.ArrayBuffer

/**
  * Created on 12/26/2017.
  *
  * @author Marek.Cervak
  */
trait GremlinCheckResultActionBuilder extends ActionBuilder {

    protected val checks: ArrayBuffer[ResultCheck] = ArrayBuffer.empty
    var extract: Option[Extractor] = Option.empty

    def check(check: ResultCheck): GremlinCheckResultActionBuilder = {
      checks += check
      this
    }

    def extractResultAndSaveAs(extractionMethod: List[Result] => String, key: String) : ActionBuilder = {
      extract = Some(Extractor(extractionMethod, key))
      this
    }
}

case class Extractor(extractionMethod: List[Result] => String, key: String){

}