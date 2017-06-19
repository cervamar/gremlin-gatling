package cz.cvut.fit.gremlin.pokec;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import cz.cvut.fit.gremlin.core.ExecutorQuery;
import cz.cvut.fit.gremlin.core.GremlinQuery;
import cz.cvut.fit.gremlin.core.GremlinQueryBuilder;
import cz.cvut.fit.gremlin.utils.TestSourceProvider;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created on 5/4/2017.
 *
 * @author Marek.Cervak
 */
@Ignore
public class PokecImporterTest {

    private static final int SIZE = 100;
    public static final String SRC_TEST_RESOURCES_POKEC = "src/test/resources/pokec/";
    final static String kryoFilePath = SRC_TEST_RESOURCES_POKEC + "idMap.ser";


    public static String FULL_USERS_PATH = "C:\\Users\\marek.cervak\\diplomka\\soc-pokec-profiles.txt\\soc-pokec-profiles.txt";
    public static String FULL_RELATIONSHIPS_PATH = "C:\\Users\\marek.cervak\\diplomka\\soc-pokec-relationships.txt\\soc-pokec-relationships.txt";

    @Test
    public void processRecords() throws Exception {
        File toProcess = new File(SRC_TEST_RESOURCES_POKEC + "profiles-" + SIZE + ".txt");
        assert(toProcess.exists());
        List<Map<String,String>> records = PokecImporter.readRecords(toProcess);
        assert (records.size() == SIZE);
    }

    @Ignore
    @Test
    public void fullLoad() throws Exception {
        File toProcess = new File(SRC_TEST_RESOURCES_POKEC + "profiles-" + SIZE + ".txt");
        assert(toProcess.exists());
        List<Map<String,String>> records = PokecImporter.readRecords(toProcess);
        Graph graph = TinkerGraph.open();
        List<PokecImporter.Record> ids = new ArrayList<>();
        for (Map<String,String> record : records) {
            Optional<PokecImporter.Record> vertex = PokecImporter.loadVertexToGraph(graph, record);
             if(vertex.isPresent()) {
                 ids.add(vertex.get());
             }
        }
        assert(IteratorUtils.count(graph.vertices()) == SIZE);
        graph.close();
    }

    @Ignore
    @Test()
    public void fullLoadGraph() throws Exception {
        TestSourceProvider.GraphSource graphSource = new TestSourceProvider.GraphInMemorySource("src/test/resources/neo4j-standalone.properties");
        //TestSourceProvider.GraphSource graphSource = new TestSourceProvider.GraphInMemorySource("src/test/resources/orientDb-inmemory.properties");
        graphSource.initGraph();

        Map<String, Object> idMap = fullLoadAndStoreVertices(graphSource.getGraph());
        graphSource.getGraph().tx().commit();
/*        Instant start = Instant.now();
        fullLoadAndStoreEdges(graphSource.getGraph(), idMap);
        Instant end = Instant.now();
        System.out.println(Duration.between(start, end));*/
        fullLoadAndStoreEdges(graphSource.getGraph(), idMap);
        graphSource.getGraph().tx().commit();
        graphSource.clean();
        PokecImporter.saveIdMaptoFile(idMap, kryoFilePath);
    }

    public Map<String, Object> fullLoadAndStoreVertices(Graph graph) throws IOException {
        return fullLoadAndStoreVertices(graph, SRC_TEST_RESOURCES_POKEC + "profiles-" + SIZE + ".txt");
    }

    public Map<String, Object> fullLoadAndStoreVertices(Graph graph, String toProcess) throws IOException {
        File verticesFile = new File(toProcess);
        assert(verticesFile.exists());
        Map<String, Object> records = PokecImporter.readAndStoreVerticesRecords(verticesFile, graph);
        return records;
    }

    public void fullLoadAndStoreEdges(Graph graph, Map<String, Object> idMap) throws IOException {
        fullLoadAndStoreEdges(graph, idMap,SRC_TEST_RESOURCES_POKEC + "relations-" + SIZE + ".txt");
    }

    public void fullLoadAndStoreEdges(Graph graph, Map<String, Object> idMap, String path) throws IOException {
        File toProcess = new File(path);
        assert(toProcess.exists());

        PokecImporter.readAndStoreEdgeRecords(toProcess, graph, idMap);

    }

    @Test
    public void testKryo() throws IOException {
        String tempPath = SRC_TEST_RESOURCES_POKEC + "temp.ser";
        Map<String, Object> ids = new HashMap<>();
        ids.put("1", "1.1");
        ids.put("2", "1.2");

        PokecImporter.saveIdMaptoFile(ids, tempPath);
        Map<String, Object> restored = PokecImporter.readIdMapFromFile(tempPath);

        assert (restored.size() == ids.size());
        assert (restored.get("1").equals(ids.get("1")));
        new File(tempPath).delete();
    }

    @Test
    public void shortestPath() throws Exception {
        Map<String, Object> idMap = PokecImporter.readIdMapFromFile(kryoFilePath);
        TestSourceProvider.GraphSource graphSource = new TestSourceProvider.GraphInMemorySource("src/test/resources/neo4j-standalone.properties");
        //TestSourceProvider.GraphSource graphSource = new TestSourceProvider.GraphInMemorySource("src/test/resources/orientDb-local.properties");

        graphSource.initGraph();

        Object id1 = idMap.get("2");
        Object id2 = idMap.get("98750");

        System.out.println(id1 + " " + id2);
        System.out.println(IteratorUtils.count(graphSource.getGraph().vertices()));
        System.out.println(graphSource.getGraph().vertices(id1).next());
        System.out.println(graphSource.getGraph().vertices(id2).next());

        //EvaluableScriptQuery executable = new GremlinQueryBuilder(graphSource.getGraph()).shortestPath(id1, id2);
       // executable.eval();
        //Object result = executable.getResult();
        GremlinQueryBuilder gremlinQueryBuilder = new GremlinQueryBuilder();
        GremlinQuery gremlinQuery = gremlinQueryBuilder.shortestPath(id1,id2);
        ExecutorQuery executorQuery = new ExecutorQuery(graphSource.getGraph());
        long startTime = System.currentTimeMillis();
        Object result = executorQuery.eval(gremlinQuery);
        long endTime = System.currentTimeMillis();
        System.out.println("execution time:" + (endTime-startTime));
        List paths = IteratorUtils.asList(result);
        System.out.println(paths);
        graphSource.clean();
    }

    @Test
    @Ignore
    public void largeDataset() throws Exception {
        //TestSourceProvider.GraphSource graphSource = new TestSourceProvider.GraphInMemorySource("src/test/resources/orientDb-local.properties");
        TestSourceProvider.GraphSource graphSource = new TestSourceProvider.GraphInMemorySource("src/test/resources/neo4j-standalone.properties");
        //TestSourceProvider.GraphSource graphSource = new TestSourceProvider.GraphInMemorySource("src/test/resources/orientDb-inmemory.properties");
        graphSource.initGraph();

        Map<String, Object> idMap = fullLoadAndStoreVertices(graphSource.getGraph(), FULL_USERS_PATH);
        if (graphSource.getGraph().features().graph().supportsTransactions()) {
            graphSource.getGraph().tx().commit();
        }
        PokecImporter.saveIdMaptoFile(idMap, kryoFilePath);


        //Map<String, Object> idMap = PokecImporter.readIdMapFromFile(kryoFilePath);
        fullLoadAndStoreEdges(graphSource.getGraph(), idMap, FULL_RELATIONSHIPS_PATH);
        if (graphSource.getGraph().features().graph().supportsTransactions()) {
            graphSource.getGraph().tx().commit();
        }


        graphSource.clean();
        //PokecImporter.saveIdMaptoFile(idMap, kryoFilePath);
    }

}