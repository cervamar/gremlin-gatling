package cz.cvut.fit.cervamar.gremlin.core;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import cz.cvut.fit.cervamar.gatling.protocol.GremlinServerClient;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.server.GremlinServer;
import org.apache.tinkerpop.gremlin.server.Settings;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created on 12/24/2017.
 *
 * @author Marek.Cervak
 */

@Ignore
public class GremlinServerClientTest {
    public static final int PORT = 8184;
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
        Settings settings = Settings.read(stream);
        settings.port = PORT;
        this.server = new GremlinServer(settings);

        server.start().join();
    }

    @After
    public void tearDown() {
        stopServer();
    }

    /**
     * Stops a current instance of Gremlin Server.
     */
    public void stopServer() {
        server.stop().join();
    }

    @Test
    public void shouldCreateGraph() throws Exception {
        GremlinServerClient gremlinServerClient = GremlinServerClient.createClient("src/main/resources/remote.yaml");
        List<Result> results = gremlinServerClient.submit("g.V().count()", Collections.emptyMap());
        assertTrue(results.size() > 0);
        assertEquals(6L, results.get(0).getLong());
    }

    private Cluster createLocalCluster() {
        return Cluster.build("localhost").port(PORT).create();
    }

}
