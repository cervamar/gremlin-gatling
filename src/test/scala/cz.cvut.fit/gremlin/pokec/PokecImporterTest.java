package cz.cvut.fit.gremlin.pokec;

import cz.cvut.fit.gremlin.sources.TestSourceProvider;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.apache.tinkerpop.shaded.kryo.Kryo;
import org.apache.tinkerpop.shaded.kryo.io.Input;
import org.apache.tinkerpop.shaded.kryo.io.Output;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
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
        List<Map<String,String>> records = PokecImporter.readRecords(toProcess);
        assert (records.size() == SIZE);
    }

    @Test
    public void fullLoad() throws Exception {
        File toProcess = new File(SRC_TEST_RESOURCES_POKEC + "profiles-" + SIZE + ".txt");
        assert(toProcess.exists());
        List<Map<String,String>> records = PokecImporter.readRecords(toProcess);
        Graph graph = TinkerGraph.open();
        List<PokecImporter.Record> ids = new ArrayList<>();
        for (Map<String,String> record : records) {
             ids.add(PokecImporter.loadVertexToGraph(graph, record));
        }
        assert(IteratorUtils.count(graph.vertices()) == SIZE);
        graph.close();
    }

    @Test
    public void fullLoadAndStoreIds() throws Exception {
        TestSourceProvider.GraphSource graphSource = new TestSourceProvider.GraphInMemorySource("src/test/resources/orientDb-inmemory.properties");
        graphSource.initGraph();
        Graph graph = graphSource.getGraph();
        File toProcess = new File(SRC_TEST_RESOURCES_POKEC + "profiles-" + SIZE + ".txt");
        assert(toProcess.exists());
        Map<String, PokecImporter.Record> records = PokecImporter.readAndStoreVerticesRecords(toProcess, graphSource.getGraph());
        assert(IteratorUtils.count(graph.vertices()) == SIZE);
        graphSource.clean();
    }

    @Test
    public void testKryo() throws Exception {
        List<PokecImporter.Record> ids = new ArrayList<>();
        ids.add(new PokecImporter.Record("1", "1.1"));
        ids.add(new PokecImporter.Record("2", "1.2"));
        ids.add(new PokecImporter.Record("3", "1.3"));
        Kryo k1 = new Kryo();
        Output output = new Output(new FileOutputStream(SRC_TEST_RESOURCES_POKEC + "filename.ser"));
        k1.writeObject(output, ids);
        output.close();

        Kryo k2 = new Kryo();
        Input listRead = new Input(new FileInputStream(SRC_TEST_RESOURCES_POKEC + "filename.ser"));
        List<PokecImporter.Record> restored = (List<PokecImporter.Record>) k2.readObject(listRead,  ArrayList.class);
        assert (restored.size() == 3);
    }


}