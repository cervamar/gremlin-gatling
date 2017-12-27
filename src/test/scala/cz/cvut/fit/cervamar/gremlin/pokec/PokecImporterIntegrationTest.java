package cz.cvut.fit.cervamar.gremlin.pokec;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

import cz.cvut.fit.cervamar.gatling.protocol.GremlinServerClient;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.server.GremlinServer;
import org.apache.tinkerpop.gremlin.server.Settings;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created on 12/27/2017.
 *
 * @author Marek.Cervak
 */
@Ignore
public class PokecImporterIntegrationTest {
    public static final String SRC_TEST_RESOURCES_POKEC = "src/test/resources/pokec/";

    public static final int PORT = 8184;
    private static GremlinServer server;
    private static GremlinServerClient gremlinServerClient;

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
        gremlinServerClient.submit("g.V().drop().iterate()");
    }

    @AfterClass
    public static void cleanUp() throws Exception {
        stopServer();
    }

    /**
     * Stops a current instance of Gremlin Server.
     */
    public static void stopServer() throws Exception {
        server.stop().join();
    }

    @Test
    public void loadGraph() throws InterruptedException, ExecutionException, IOException {
        PokecImporter pokecImporter = new PokecImporter(gremlinServerClient);
        pokecImporter.loadVerticesToServer(SRC_TEST_RESOURCES_POKEC + "/" + "profiles-100.txt");
        pokecImporter.loadEdgesToServer(SRC_TEST_RESOURCES_POKEC + "/" + "relations-100.txt");
        List<Result> results = gremlinServerClient.submit("g.V().count()");
        assertTrue(results.size() > 0);
        assertEquals(100L, results.get(0).getLong());
        results = gremlinServerClient.submit("g.E().count()");
        assertTrue(results.size() > 0);
        assertEquals(82L, results.get(0).getLong());
    }
}
