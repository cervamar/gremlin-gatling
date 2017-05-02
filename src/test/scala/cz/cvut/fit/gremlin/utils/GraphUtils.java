package cz.cvut.fit.gremlin.utils;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.io.IoCore;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;

import java.io.IOException;

/**
 * Created on 5/2/2017.
 *
 * @author Marek.Cervak
 */
public class GraphUtils {

    public static void importModern(Graph graph) throws IOException {
        graph.io(IoCore.gryo()).readGraph("src/test/resources/tinkerpop-modern.kryo");
    }

    public static boolean isEmpty(Graph graph) {
        return IteratorUtils.count(graph.vertices()) == 0;
    }
}
