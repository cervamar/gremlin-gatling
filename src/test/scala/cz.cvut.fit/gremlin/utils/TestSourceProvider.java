package cz.cvut.fit.gremlin.utils;

import org.apache.tinkerpop.gremlin.neo4j.structure.Neo4jGraph;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created on 5/3/2017.
 *
 * @author Marek.Cervak
 */
public interface TestSourceProvider {
    void initGraph();
    Graph getGraph();
    void clean() throws Exception;

    default void fill() throws IOException {
        Graph graph = getGraph();
        if (GraphUtils.isEmpty(graph)) {
            GraphUtils.importModern(graph);
        }
    }

    default Map<String,Object> updateIds() {
        Map<String,Object> vertices = new HashMap<>();
        for (Iterator<Vertex> it = getGraph().vertices(); it.hasNext(); ) {
            Vertex vertex = it.next();
            vertices.put((String) vertex.property("name").value(), vertex.id());
        }
        return vertices;
    }



    abstract class GraphSource implements TestSourceProvider {
        protected Graph graph;

        @Override
        public Graph getGraph() {
            return graph;
        }

        @Override
        public void clean() throws Exception {
            getGraph().close();
        }

    }

    class GraphInMemorySource extends GraphSource {
        private String configurationFile;
        public GraphInMemorySource(String path) {
            configurationFile = path;
        }

        @Override
        public void initGraph() {
            graph = GraphFactory.open(configurationFile);
        }
    }

    abstract class GraphFileSystemSource extends GraphSource {

        protected String dbPath = "target/localdb";

        @Override
        public void clean() throws Exception {
            getGraph().close();
            Path rootPath = Paths.get("target/localdb");
            Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .peek(System.out::println)
                    .forEach(File::delete);
        }
    }


   class Neo4JSource extends GraphFileSystemSource {

       @Override
       public void initGraph() {
           graph = Neo4jGraph.open(dbPath);
        }

   }
}
