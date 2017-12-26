package cz.cvut.fit.gremlin.core;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import cz.cvut.fit.gatling.protocol.GremlinServerClient;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.server.GremlinServer;
import org.apache.tinkerpop.gremlin.server.Settings;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created on 12/24/2017.
 *
 * @author Marek.Cervak
 */
public class GremlinServerClientTest {
    private GremlinServer server;

    @Before
    public void setUp() throws Exception {
        startServer();
    }

    /**
     * Starts a new instance of Gremlin Server.
     */
    public void startServer() throws Exception {
        final InputStream stream = GremlinServerClient.class.getClassLoader().getResourceAsStream("conf/gremlin-server-modern.yaml");
        this.server = new GremlinServer(Settings.read(stream));

        server.start().join();
    }

    @After
    public void tearDown() throws Exception {
        stopServer();
    }

    /**
     * Stops a current instance of Gremlin Server.
     */
    public void stopServer() throws Exception {
        server.stop().join();
    }

    @Test
    public void shouldCreateGraph() throws Exception {
        GremlinServerClient gremlinServerClient = GremlinServerClient.createGremlinServerClient();
        List<Result> results = gremlinServerClient.submit("g.V().count()", Collections.emptyMap());
        System.out.println(results.size());
    }

    @Test
    public void getVertexById() throws Exception {
        GremlinServerClient gremlinServerClient = GremlinServerClient.createGremlinServerClient();
        List<Result> results = gremlinServerClient.submit("g.V(7)", Collections.emptyMap());
        System.out.println(results.size());
    }
}
