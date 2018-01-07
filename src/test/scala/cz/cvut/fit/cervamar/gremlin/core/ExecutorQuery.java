package cz.cvut.fit.cervamar.gremlin.core;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import io.gatling.core.session.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine;
import org.apache.tinkerpop.gremlin.structure.Graph;

/**
 * Created on 1/6/2018.
 *
 * @author Marek.Cervak
 */
@Slf4j
public class ExecutorQuery {

    private static final String GRAPH = "graph";
    private static final String TRAVERSAL = "g";
    private final boolean supportNumericIds;
    private ScriptEngine engine = new GremlinGroovyScriptEngine();


    public ExecutorQuery(Graph graph, boolean supportNumericIds) {
        this.supportNumericIds = supportNumericIds;
        engine.put(GRAPH, graph);
        engine.put(TRAVERSAL, graph.traversal());
    }

    Object eval(GremlinQuery gremlinQuery, Session session) throws ScriptException {
        ResolvedQuery resolvedQuery = gremlinQuery.getResolvedQuery(session, supportNumericIds);
        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        resolvedQuery.bindings().forEach(bindings::put);
        log.debug("Query {} with bindings {}", resolvedQuery.query(), resolvedQuery.bindings());
        return engine.eval(resolvedQuery.query(), bindings);
    }
}
