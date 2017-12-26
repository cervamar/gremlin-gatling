package cz.cvut.fit.cervamar

import io.gatling.core.check.Check
import org.apache.tinkerpop.gremlin.driver.Result

/**
  * Created on 12/26/2017.
  *
  * @author Marek.Cervak
  */
package object gatling {
  type ResultCheck = Check[List[Result]]
}
