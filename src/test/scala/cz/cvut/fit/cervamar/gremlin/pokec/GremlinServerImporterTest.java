package cz.cvut.fit.cervamar.gremlin.pokec;

import java.io.InputStream;
import java.util.Collections;

import cz.cvut.fit.cervamar.gatling.protocol.GremlinServerClient;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.server.GremlinServer;
import org.apache.tinkerpop.gremlin.server.Settings;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created on 12/24/2017.
 *
 * @author Marek.Cervak
 */

public class GremlinServerImporterTest extends GremlinServerTestBase {
    public static final int PORT = 8184;
    private static GremlinServer server;

    @BeforeClass
    public static void setUp() throws Exception {
        startServer();
        gremlinServerClient = new GremlinServerClient(Cluster.build("localhost").port(PORT).create());
    }

    /**
     * Starts a new instance of Gremlin Server.
     */
    public static void startServer() throws Exception {
        final InputStream stream = GremlinServerClient.class.getClassLoader().getResourceAsStream("conf/gremlin-server.yaml");
        Settings settings = Settings.read(stream);
        settings.port = PORT;
        server = new GremlinServer(settings);

        server.start().join();
    }

    @Before
    public void tearDown() throws Exception {
        clearGraph();
    }

    @AfterClass
    public static void cleanUp() throws Exception {
        stopServer();
    }

    /**
     * Stops a current instance of Gremlin Server.
     */
    public static void stopServer() {
        server.stop().join();
    }

    @Test
    public void insertMultipleVertices() throws Exception {
        gremlinServerClient.submit("graph.addVertex('id', '1', 'name', 'jarda')\n" +
                "graph.addVertex('id', '2', 'name', 'karel')", Collections.emptyMap());
        assertEquals(2L, countVertices());
    }

    @Test
    public void addEdgeBetweenVerticesByTheirIds() throws Exception {
        gremlinServerClient.submit("graph.addVertex('id', '1', 'name', 'jarda')\n" +
                "graph.addVertex('id', '2', 'name', 'karel')", Collections.emptyMap());
        gremlinServerClient.submit("v1 = g.V().has('id', '1').next()\n" +
                "v2 = g.V().has('id', '1').next()\n" +
                "v1.addEdge('likes', v2)");
        assertEquals(2L, countVertices());
        assertEquals(1L, countEdges());
    }
}
