package cz.cvut.fit.cervamar.gatling.check

import cz.cvut.fit.cervamar.gatling.ResultCheck
import io.gatling.commons.validation.{Failure, Validation}
import io.gatling.core.check.CheckResult
import io.gatling.core.session.Session
import org.apache.tinkerpop.gremlin.driver.Result

import scala.collection.mutable

/**
  * Created on 12/26/2017.
  *
  * @author Marek.Cervak
  */

case class SimpleResultCheck (func:List[Result] => Boolean) extends ResultCheck {
  override def check(response:List[Result],session:Session)(implicit cache:mutable.Map[Any,Any]):Validation[CheckResult]={
    if(func(response)){
      CheckResult.NoopCheckResultSuccess
    }else{
      Failure("Gremlin Result check Failed")
    }
  }
}