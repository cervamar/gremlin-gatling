package cz.cvut.fit.gremlin.core;

import cz.cvut.fit.gremlin.sources.TestSourceProvider;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
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
public class GremlinQueryBuilderTest {
    Map<String, Object> vertices = new HashMap<>();

    @Parameterized.Parameter
    public TestSourceProvider sourceProvider;

    @Parameterized.Parameters
    public static Collection<TestSourceProvider> properties () {
        return Arrays.asList(new TestSourceProvider.GraphInMemorySource("src/test/resources/tinkerpop-modern.properties"),
                new TestSourceProvider.GraphInMemorySource("src/test/resources/orientDb-inmemory.properties"),
                new TestSourceProvider.GraphInMemorySource("src/test/resources/janusGraph-inmemory.properties"),
                new TestSourceProvider.Neo4JSource());
    }

    @Before
    public void fillDatabase() throws IOException {
        sourceProvider.initGraph();
        sourceProvider.fill();
        vertices = sourceProvider.updateIds();
    }

    @Test
    public void shortestPath() throws ScriptException {
        EvaluableScriptQuery compiledScript = new GremlinQueryBuilder(sourceProvider.getGraph()).shortestPath(vertices.get("marko"),vertices.get("ripple"));
        Object result = compiledScript.eval();
        List paths = IteratorUtils.asList(result);
        assert(paths.size() > 0);
        assert(((Path) paths.get(0)).size() == 3);
    }


    @Test
    public void ageMean() throws ScriptException {
        EvaluableScriptQuery compiledScript = new GremlinQueryBuilder(sourceProvider.getGraph()).query("g.V().values('age').mean()");
        Object result = compiledScript.eval();
        List mean = IteratorUtils.asList(result);
        assert(mean.size() > 0);
        assert(((double) mean.get(0)) == 30.75);
    }

    @After
    public void cleanDatabase() throws Exception {
        sourceProvider.clean();
    }

}
