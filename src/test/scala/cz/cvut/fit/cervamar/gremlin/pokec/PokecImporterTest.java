package cz.cvut.fit.cervamar.gremlin.pokec;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import cz.cvut.fit.cervamar.gatling.protocol.GremlinServerClient;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import static cz.cvut.fit.cervamar.gremlin.pokec.PokecImporter.ID;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created on 5/4/2017.
 *
 * @author Marek.Cervak
 */
public class PokecImporterTest {

    public static final String SRC_TEST_RESOURCES_POKEC = "src/test/resources/pokec/";

    private PokecImporter pokecImporter;
    private GremlinServerClient serverClient;

    private static GremlinServerClient mockServerClient() {
        return mock(GremlinServerClient.class);
    }

    @Before
    public void setUp() {
        serverClient = mockServerClient();
        pokecImporter = new PokecImporter(serverClient);
    }

    @Test
    public void createVertexRecord() throws IOException {
        String record = FileUtils.readFileToString(new File(SRC_TEST_RESOURCES_POKEC + "/" + "profiles-1.txt"));
        Map<String, String> vertex = PokecImporter.createVertexRecord(record);
        assertEquals(4, vertex.size());
        assertEquals("1", vertex.get(ID));
        assertEquals("1", vertex.get("gender"));
        assertEquals("zilinsky kraj, zilina", vertex.get("region"));
        assertEquals("26", vertex.get("age"));
    }

    @Test
    public void createEdgeRecord() throws IOException {
        String record = FileUtils.readFileToString(new File(SRC_TEST_RESOURCES_POKEC + "/" + "relations-1.txt"));
        EdgeWrapper edge = PokecImporter.createEdgeRecord(record);
        assertEquals("1", edge.getFrom());
        assertEquals("13", edge.getTo());
    }

    @Test
    public void loadVerticesToServer() throws InterruptedException, ExecutionException, IOException {
        pokecImporter.loadVerticesToServer(SRC_TEST_RESOURCES_POKEC + "/" + "profiles-100.txt");
        verify(serverClient, times(100)).submit(any(), any());
    }

    @Test
    public void loadEdgesToServer() throws InterruptedException, ExecutionException, IOException {
        pokecImporter.loadEdgesToServer(SRC_TEST_RESOURCES_POKEC + "/" + "relations-100.txt");
        verify(serverClient, times(100)).submit(any(), any());
    }

    @Test
    public void relationsOutOfRangeShouldBeFiltered() throws InterruptedException, ExecutionException, IOException {
        pokecImporter.loadEdgesToServer(SRC_TEST_RESOURCES_POKEC + "/" + "relations-out-of-range.txt");
        verify(serverClient, times(2)).submit(any(), any());
    }

    @Test
    public void profilesOutOfRangeShouldBeFiltered() throws InterruptedException, ExecutionException, IOException {
        pokecImporter.loadVerticesToServer(SRC_TEST_RESOURCES_POKEC + "/" + "profiles-out-of-range.txt");
        verify(serverClient, times(1)).submit(any(), any());
    }
}