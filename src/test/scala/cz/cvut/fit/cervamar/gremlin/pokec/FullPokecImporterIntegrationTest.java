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
        gremlinServerClient = GremlinServerClient.createDefaultClient();
    }

    @Test
    public void loadGraph() throws InterruptedException, ExecutionException, IOException {
        clearGraph();
        //must be empty
        assertEquals(0L, countVertices());
        assertEquals(0L, countEdges());

        PokecImporter pokecImporter = new PokecImporter(gremlinServerClient);
        long startTime = System.currentTimeMillis();

        pokecImporter.loadVerticesToServer(SRC_TEST_RESOURCES_POKEC + "soc-pokec-profiles/profiles-1000.txt");
        //pokecImporter.loadVerticesToServer(SRC_TEST_RESOURCES_POKEC + "soc-pokec-profiles/soc-pokec-profiles.txt");
        //pokecImporter.loadEdgesToServer(SRC_TEST_RESOURCES_POKEC + "soc-pokec-relationships/soc-pokec-relationships.txt");
        pokecImporter.loadEdgesToServer(SRC_TEST_RESOURCES_POKEC + "soc-pokec-relationships/relations-1000.txt");

        long endTime = System.currentTimeMillis();
        System.out.println("That took " + (endTime - startTime) + " milliseconds");

        assertEquals(1000L, countVertices());
        assertEquals(841L, countEdges());
    }
    }
