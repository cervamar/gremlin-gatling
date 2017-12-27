package cz.cvut.fit.cervamar.gremlin.pokec;

import java.util.List;
import java.util.concurrent.ExecutionException;

import cz.cvut.fit.cervamar.gatling.protocol.GremlinServerClient;
import org.apache.tinkerpop.gremlin.driver.Result;

import static org.junit.Assert.assertTrue;

/**
 * Created on 12/27/2017.
 *
 * @author Marek.Cervak
 */
public abstract class GremlinServerTestBase {

    protected static GremlinServerClient gremlinServerClient;

    protected long countEdges() throws ExecutionException, InterruptedException {
        List<Result> results = gremlinServerClient.submit("g.E().count()");
        assertTrue(results.size() > 0);
        return results.get(0).getLong();
    }

    protected long countVertices() throws ExecutionException, InterruptedException {
        List<Result> results = gremlinServerClient.submit("g.V().count()");
        assertTrue(results.size() > 0);
        return results.get(0).getLong();
    }

    protected void clearGraph() throws ExecutionException, InterruptedException {
        gremlinServerClient.submit("g.V().drop().iterate()");
    }
}
