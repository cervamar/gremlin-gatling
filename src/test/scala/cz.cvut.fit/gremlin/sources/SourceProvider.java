package cz.cvut.fit.gremlin.sources;

import org.apache.tinkerpop.gremlin.structure.Graph;

/**
 * Created on 5/3/2017.
 *
 * @author Marek.Cervak
 */
public interface SourceProvider {
    Graph getGraph();
    default void clean() throws Exception {
        getGraph().close();
    };


}
