package cz.cvut.fit.cervamar.gatling.util

import java.util

/**
  * Created on 12/30/2017.
  *
  * @author Marek.Cervak
  */
object ScalaUtils {
  def createJavaMap(scalaMap: Map[String, Object]): util.Map[String, Object] = {
    val map = new util.HashMap[String, Object]()
    scalaMap.foreach((entry) => map.put(entry._1, entry._2))
    map
  }
}
