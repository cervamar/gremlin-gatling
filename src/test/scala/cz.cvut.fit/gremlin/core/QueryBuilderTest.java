package cz.cvut.fit.gremlin.core;

import cz.cvut.fit.gremlin.utils.GraphUtils;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.*;

/**
 * Created on 5/3/2017.
 *
 * @author Marek.Cervak
 */
@RunWith(Parameterized.class)
public class QueryBuilderTest {
    Map<String, Object> vertices = new HashMap<>();
    public Graph graph;

    @Parameterized.Parameter
    public String propertyFile;

    @Parameterized.Parameters
    public static Collection<String> properties () {
        return Arrays.asList("src/test/resources/tinkerpop-modern.properties", "src/test/resources/orientDb-inmemory.properties");
    }

    @Before
    public void fillDatabase() throws IOException {
        graph = GraphFactory.open(propertyFile);
        if (GraphUtils.isEmpty(graph)) {
            GraphUtils.importModern(graph);
        }
        updateIds();
    }

    private void updateIds() {
        for (Iterator<Vertex> it = graph.vertices(); it.hasNext(); ) {
            Vertex vertex = it.next();
            vertices.put((String) vertex.property("name").value(), vertex.id());
        }
    }

    @Test
    public void shortestPath() throws ScriptException {
        EvaluableScriptQuery compiledScript = new QueryBuilder(graph).shortestPath(vertices.get("marko"),vertices.get("ripple"));
        Object result = compiledScript.eval();
        List paths = IteratorUtils.asList(result);
        assert(paths.size() > 0);
        assert(((Path) paths.get(0)).size() == 3);
    }

    @After
    public void cleanDatabase() throws Exception {
        graph.traversal().V().drop().iterate();
        graph.close();
    }

}
