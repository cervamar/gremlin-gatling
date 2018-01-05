package cz.cvut.fit.cervamar.gremlin.core;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cvut.fit.cervamar.gremlin.utils.MockedSession;
import cz.cvut.fit.cervamar.gremlin.utils.TestSourceProvider;
import io.gatling.core.session.Session;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Created on 5/3/2017.
 *
 * @author Marek.Cervak
 */
@Ignore
@RunWith(Parameterized.class)
public class GremlinQueryBuilderTest {
    private Map<String, Object> vertices = new HashMap<>();

    private GremlinQueryBuilder gremlinQueryBuilder = new GremlinQueryBuilder();
    private ExecutorQuery executorQuery;

    @Parameterized.Parameter
    public TestSourceProvider sourceProvider;
    private Session mockedSession = new MockedSession().createSession();

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
        executorQuery = new ExecutorQuery(sourceProvider.getGraph());
    }

    @Test
    public void shortestPath() {
        GremlinQuery query = gremlinQueryBuilder.shortestPath(vertices.get("marko").toString(), vertices.get("ripple").toString());
        Object result = executorQuery.eval(query, mockedSession);
        List paths = IteratorUtils.asList(result);
        assert(paths.size() > 0);
        assert(((Path) paths.get(0)).size() == 3);
    }


    @Test
    public void ageMean() {
        GremlinQuery query = gremlinQueryBuilder.query("g.V().values('age').mean()");
        Object result = executorQuery.eval(query, mockedSession);
        List mean = IteratorUtils.asList(result);
        assert(mean.size() == 1);
        assert(((double) mean.get(0)) == 30.75);
    }

    @Test
    public void getNeighbors() {
        GremlinQuery query = gremlinQueryBuilder.neighbors(vertices.get("marko").toString(), 1);
        Object result = executorQuery.eval(query, mockedSession);
        List results = IteratorUtils.asList(result);
        assert(results.size() == 3);
        query = gremlinQueryBuilder.neighbors(vertices.get("marko").toString(), 2);
        result = executorQuery.eval(query, mockedSession);
        results = IteratorUtils.asList(result);
        assert(results.size() == 2);
    }

    @Test
    public void getMutualNeighbors() {
        GremlinQuery query = gremlinQueryBuilder.mutualNeighbors(vertices.get("marko").toString(), vertices.get("josh").toString());
        Object result = executorQuery.eval(query, mockedSession);
        List results = IteratorUtils.asList(result);
        assert(results.size() == 1);
        query = gremlinQueryBuilder.mutualNeighbors(vertices.get("marko").toString(), vertices.get("ripple").toString());
        result = executorQuery.eval(query, mockedSession);
        results = IteratorUtils.asList(result);
        assert(results.size() == 0);
    }

    @Test
    public void getVertex() {
        GremlinQuery query = gremlinQueryBuilder.getVertex(vertices.get("marko").toString());
        Object result = executorQuery.eval(query, mockedSession);
        List results = IteratorUtils.asList(result);
        assert(results.size() == 1);
    }

    @After
    public void cleanDatabase() throws Exception {
        sourceProvider.clean();
    }

}