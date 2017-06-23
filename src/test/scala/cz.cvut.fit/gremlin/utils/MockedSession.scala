package cz.cvut.fit.gremlin.utils

import io.gatling.core.session.Session

/**
  * Created on 6/22/2017.
  *
  * @author Marek.Cervak
  */
class MockedSession {

  def createSession() : Session = {
    Session(scenario = "Test", userId = 1L)
  }

}
