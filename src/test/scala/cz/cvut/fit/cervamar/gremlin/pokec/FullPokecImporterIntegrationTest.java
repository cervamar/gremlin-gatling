package cz.cvut.fit.cervamar.gremlin.pokec;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import cz.cvut.fit.cervamar.gatling.protocol.GremlinServerClient;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created on 12/27/2017.
 *
 * @author Marek.Cervak
 */
@Ignore
public class FullPokecImporterIntegrationTest extends GremlinServerTestBase {
    public static final String SRC_TEST_RESOURCES_POKEC = "C:/Users/marek.cervak/diplomka/";

    @BeforeClass
    public static void setUp() throws Exception {
        gremlinServerClient = GremlinServerClient.createGremlinServerClient();
    }

    @Test
    public void loadGraph() throws InterruptedException, ExecutionException, IOException {
        clearGraph();
        //must be empty
        assertEquals(0L, countVertices());
        assertEquals(0L, countEdges());

        PokecImporter pokecImporter = new PokecImporter(gremlinServerClient);
        pokecImporter.loadVerticesToServer(SRC_TEST_RESOURCES_POKEC + "soc-pokec-profiles/soc-pokec-profiles.txt");
        pokecImporter.loadEdgesToServer(SRC_TEST_RESOURCES_POKEC + "soc-pokec-relationships/relations-100.txt");

        assertEquals(100L, countVertices());
        assertEquals(82L, countEdges());
    }
}
