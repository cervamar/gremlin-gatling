package cz.cvut.fit.gremlin.pokec;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created on 5/4/2017.
 *
 * @author Marek.Cervak
 */
public class PokecImporterTest {

    private static final int SIZE = 100;
    public static final String SRC_TEST_RESOURCES_POKEC = "src/test/resources/pokec/";

    @Test
    public void processRecords() throws Exception {
        File toProcess = new File(SRC_TEST_RESOURCES_POKEC + "profiles-" + SIZE + ".txt");
        assert(toProcess.exists());
        List<Map<String,String>> records = PokecImporter.processRecords(toProcess);
        assert (records.size() == SIZE);
    }

    @Test
    public void fullLoad() throws Exception {
        File toProcess = new File(SRC_TEST_RESOURCES_POKEC + "profiles-" + SIZE + ".txt");
        assert(toProcess.exists());
        List<Map<String,String>> records = PokecImporter.processRecords(toProcess);
        Graph graph = TinkerGraph.open();
        for (Map<String,String> record : records) {
            PokecImporter.loadVertexToGraph(graph, record);
        }
        assert(IteratorUtils.count(graph.vertices()) == SIZE);
        graph.close();
    }


}